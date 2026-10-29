/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal;

import jakarta.annotation.Nullable;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.schema.extract.spi.ColumnTypeInformation;
import org.hibernate.type.BasicType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicPluralJavaType;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayJavaType;
import org.hibernate.type.descriptor.jdbc.AggregateJdbcType;
import org.hibernate.type.descriptor.jdbc.ArrayJdbcType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeConstructor;
import org.hibernate.type.spi.TypeConfiguration;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;

public class MilvusArrayJdbcType extends ArrayJdbcType {

	private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
	private static final int MAX_OFFSET_SECONDS = 18 * 60 * 60;

	public MilvusArrayJdbcType(JdbcType elementJdbcType) {
		super( elementJdbcType );
	}

	@Override
	protected <T,E> Object[] convertToArray(
			BasicBinder<T> binder,
			ValueBinder<E> elementBinder,
			BasicPluralJavaType<E> pluralJavaType,
			T value,
			WrapperOptions options)
			throws SQLException {
		final var javaType = binder.getJavaType();
		final var elementJavaType = pluralJavaType.getElementJavaType();
		final var result = javaType.unwrap( value, Object[].class, options );
		for ( int i = 0; i < result.length; i++ ) {
			if ( result[i] != null ) {
				result[i] = elementBinder.getBindValue( elementJavaType.cast( result[i] ), options );
			}
		}
		return result;
	}

	@Override
	protected <X> X getArray(BasicExtractor<X> extractor, Array array, WrapperOptions options) throws SQLException {
		final var javaType = extractor.getJavaType();
		return array != null
			&& getElementJdbcType() instanceof AggregateJdbcType aggregateJdbcType
			&& aggregateJdbcType.getEmbeddableMappingType() != null
				? super.getArray( extractor, array, options )
				: javaType.wrap( toJavaArray( array, getElementJdbcType() ), options );
	}

	private static Object toJavaArray(@Nullable Array array, JdbcType expectedElementType) throws SQLException {
		if ( array == null ) {
			return null;
		}
		final Object javaArray = array.getArray();
		// Milvus doesn't support binary data types, so we encode this as strings, which we need to decode here
		// Similarly, Milvus doesn't support certain types in arrays, so we decode them here as well
		if ( array.getBaseType() == SqlTypes.VARCHAR ) {
			if ( expectedElementType.isBinary() ) {
				final Object[] objectArray = (Object[]) javaArray;
				for ( int i = 0; i < objectArray.length; i++ ) {
					objectArray[i] = PrimitiveByteArrayJavaType.INSTANCE.fromString( (String) objectArray[i] );
				}
			}
			else if ( expectedElementType.isTemporal() ) {
				final Object[] objectArray = (Object[]) javaArray;
				for ( int i = 0; i < objectArray.length; i++ ) {
					if ( objectArray[i] != null ) {
						final TemporalAccessor parsed = DateTimeUtils.DATE_TIME.parse( (String) objectArray[i] );
						if ( expectedElementType.getDefaultSqlTypeCode() == SqlTypes.TIME_UTC ) {
							objectArray[i] = toOffsetTime( LocalDateTime.from( parsed ).toInstant( ZoneOffset.UTC ).toEpochMilli() );
						}
						else {
							if ( parsed.query( TemporalQueries.offset() ) != null ) {
								objectArray[i] = OffsetDateTime.from( parsed );
							}
							else {
								objectArray[i] = new Timestamp( LocalDateTime.from( parsed ).toInstant( ZoneOffset.UTC ).toEpochMilli() );
							}
						}
					}
				}
			}
		}
		return array;
	}

	private static OffsetTime toOffsetTime(long epochMillis) {
		final ZoneOffset offset;
		if ( epochMillis < 0 ) {
			offset = ZoneOffset.ofTotalSeconds( MAX_OFFSET_SECONDS );
		}
		else if ( epochMillis >= DAY_IN_MILLIS ) {
			offset = ZoneOffset.ofTotalSeconds( -MAX_OFFSET_SECONDS );
		}
		else {
			offset = ZoneOffset.UTC;
		}
		return Instant.ofEpochMilli( epochMillis )
				.atOffset( offset )
				.toOffsetTime();
	}

	public static class MilvusArrayJdbcTypeConstructor implements JdbcTypeConstructor {
		public static final MilvusArrayJdbcTypeConstructor INSTANCE = new MilvusArrayJdbcTypeConstructor();

		public JdbcType resolveType(
				TypeConfiguration typeConfiguration,
				Dialect dialect,
				BasicType<?> elementType,
				ColumnTypeInformation columnTypeInformation) {
			return resolveType( typeConfiguration, dialect, elementType.getJdbcType(), columnTypeInformation );
		}

		public JdbcType resolveType(
				TypeConfiguration typeConfiguration,
				Dialect dialect,
				JdbcType elementType,
				ColumnTypeInformation columnTypeInformation) {
			return new MilvusArrayJdbcType( elementType );
		}

		@Override
		public int getDefaultSqlTypeCode() {
			return Types.ARRAY;
		}
	}
}
