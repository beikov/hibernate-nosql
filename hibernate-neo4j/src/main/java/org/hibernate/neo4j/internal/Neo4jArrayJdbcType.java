/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import jakarta.annotation.Nullable;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.tool.schema.extract.spi.ColumnTypeInformation;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.BasicPluralJavaType;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.AggregateJdbcType;
import org.hibernate.type.descriptor.jdbc.ArrayJdbcType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeConstructor;
import org.hibernate.type.spi.TypeConfiguration;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;

public class Neo4jArrayJdbcType extends ArrayJdbcType {
	public Neo4jArrayJdbcType(JdbcType elementJdbcType) {
		super( elementJdbcType );
	}

	@Override
	protected String getElementTypeName(JavaType<?> javaType, SharedSessionContractImplementor session) {
		// Neo4j's jdbc driver expects the element type name to be in uppercase
		// see https://github.com/neo4j/neo4j-jdbc/blob/b0f5eabdbde60c5f03695f02f9df4a8f21beb95f/neo4j-jdbc/src/main/java/org/neo4j/jdbc/ArrayImpl.java#L64
		return super.getElementTypeName( javaType, session ).toUpperCase( Locale.ROOT );
	}

	@Override
	protected <X> X getArray(BasicExtractor<X> extractor, Array array, WrapperOptions options) throws SQLException {
		final var javaType = extractor.getJavaType();
		return array != null
			&& getElementJdbcType() instanceof AggregateJdbcType aggregateJdbcType
			&& aggregateJdbcType.getEmbeddableMappingType() != null
				? super.getArray( extractor, array, options )
				: javaType.wrap( toJavaArray( array, javaType.getJavaTypeClass(),
						elementJavaTypeClass( javaType ) ), options );
	}

	private static Class<?> elementJavaTypeClass(JavaType<?> javaType) {
		return javaType instanceof BasicPluralJavaType<?> basicPluralJavaType
				? basicPluralJavaType.getElementJavaType().getJavaTypeClass()
				: javaType.getJavaTypeClass().getComponentType();
	}

	@Override
	public <X> ValueBinder<X> getBinder(final JavaType<X> javaTypeDescriptor) {
		return new Binder<>( javaTypeDescriptor,
				(BasicPluralJavaType<?>) javaTypeDescriptor );
	}

	private class Binder<X,E> extends BasicBinder<X> {
		private final BasicPluralJavaType<E> pluralJavaType;

		private Binder(JavaType<X> javaType, BasicPluralJavaType<E> pluralJavaType) {
			super( javaType, Neo4jArrayJdbcType.this );
			this.pluralJavaType = pluralJavaType;
		}

		@Override
		protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
				throws SQLException {
			st.setObject( index, getBindValue( value, options ) );
		}

		@Override
		protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
				throws SQLException {
			final var array = getBindValue( value, options );
			try {
				st.setObject( name, array );
			}
			catch (SQLException ex) {
				throw new HibernateException(
						"JDBC driver does not support named parameters for setArray. Use positional.", ex );
			}
		}

		@Override
		public Object[] getBindValue(X value, WrapperOptions options) throws SQLException {
			final var elementBinder = getElementJdbcType().getBinder( pluralJavaType.getElementJavaType() );
			return convertToArray( this, elementBinder, pluralJavaType, value, options );
		}
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
				if ( result[i] instanceof Instant instant ) {
					result[i] = instant.atZone( ZoneOffset.UTC );
				}
				else if ( result[i] instanceof Timestamp sqlTimestamp ) {
					result[i] = sqlTimestamp.toLocalDateTime();
				}
				else if ( result[i] instanceof Date sqlDate ) {
					result[i] = sqlDate.toLocalDate();
				}
				else if ( result[i] instanceof Time sqlTime ) {
					result[i] = sqlTime.toLocalTime();
				}
			}
		}
		return result;
	}

	private static Object toJavaArray(@Nullable java.sql.Array array, Class<?> expectedArrayType, Class<?> elementJavaType) throws SQLException {
		// The Neo4j driver seems to produce a long[] for integral arrays, regardless of the type of the written value,
		// so we try to do some coercion in here
		if ( array == null ) {
			return null;
		}
		if ( Types.BIGINT == array.getBaseType() ) {
			long[] longs = new long[128];
			try (ResultSet rs = array.getResultSet()) {
				int i = 0;
				while ( rs.next() ) {
					if ( i >= longs.length ) {
						longs = Arrays.copyOf( longs, longs.length << 1 );
					}
					longs[rs.getInt( 1 ) - 1] = rs.getLong( 2 );
					i++;
				}
				longs = Arrays.copyOf( longs, i );
			}
			if ( expectedArrayType == byte[].class ) {
				final byte[] bytes = new byte[longs.length];
				for ( int i = 0; i < longs.length; i++ ) {
					bytes[i] = (byte) longs[i];
				}
				return bytes;
			}
			else if ( expectedArrayType == short[].class ) {
				final short[] shorts = new short[longs.length];
				for ( int i = 0; i < longs.length; i++ ) {
					shorts[i] = (short) longs[i];
				}
				return shorts;
			}
			else if ( expectedArrayType == int[].class ) {
				final int[] ints = new int[longs.length];
				for ( int i = 0; i < longs.length; i++ ) {
					ints[i] = (int) longs[i];
				}
				return ints;
			}
			else if ( expectedArrayType == long[].class ) {
				return longs;
			}
			else {
				final Object[] objects = new Object[longs.length];
				for ( int i = 0; i < longs.length; i++ ) {
					objects[i] = longs[i];
				}
				return objects;
			}
		}
		else if ( Types.DOUBLE == array.getBaseType() ) {
			double[] doubles = new double[128];
			try (ResultSet rs = array.getResultSet()) {
				int i = 0;
				while ( rs.next() ) {
					if ( i >= doubles.length ) {
						doubles = Arrays.copyOf( doubles, doubles.length << 1 );
					}
					doubles[rs.getInt( 1 ) - 1] = rs.getDouble( 2 );
					i++;
				}
				doubles = Arrays.copyOf( doubles, i );
			}
			if ( expectedArrayType == float[].class ) {
				final float[] floats = new float[doubles.length];
				for ( int i = 0; i < doubles.length; i++ ) {
					floats[i] = (float) doubles[i];
				}
				return floats;
			}
			else if ( expectedArrayType == double[].class ) {
				return doubles;
			}
			else {
				final Object[] objects = new Object[doubles.length];
				for ( int i = 0; i < doubles.length; i++ ) {
					objects[i] = doubles[i];
				}
				return objects;
			}
		}
		else if ( Types.OTHER == array.getBaseType() ) {
			final Class<?> resultType = "DURATION".equals( array.getBaseTypeName() )
					? Duration.class
					: "UUID".equals( array.getBaseTypeName() )
							? UUID.class
							: Object.class;
			try (ResultSet rs = array.getResultSet()) {
				Object[] objects = new Object[128];
				int i = 0;
				while ( rs.next() ) {
					if ( i >= objects.length ) {
						objects = Arrays.copyOf( objects, objects.length << 1 );
					}
					objects[rs.getInt( 1 ) - 1] = rs.getObject( 2, resultType );
					i++;
				}
				return Arrays.copyOf( objects, i );
			}
		}
		else if ( array.getBaseType() == Types.TIMESTAMP_WITH_TIMEZONE && elementJavaType == Instant.class ) {
			// InstantJavaType unfortunately misses a branch to convert from ZonedDateTime to Instant, so we do it here
			final Object[] javaArray = (Object[]) array.getArray();
			for ( int i = 0; i < javaArray.length; i++ ) {
				if ( javaArray[i] instanceof ZonedDateTime zonedDateTime ) {
					javaArray[i] = zonedDateTime.toInstant();
				}
			}
			return javaArray;
		}
		else {
			return array.getArray();
		}
	}

	public static class Neo4jArrayJdbcTypeConstructor implements JdbcTypeConstructor {
		public static final Neo4jArrayJdbcTypeConstructor INSTANCE = new Neo4jArrayJdbcTypeConstructor();

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
			return new Neo4jArrayJdbcType( elementType );
		}

		@Override
		public int getDefaultSqlTypeCode() {
			return Types.ARRAY;
		}
	}
}
