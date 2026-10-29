/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import jakarta.persistence.TemporalType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcLiteralFormatter;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.internal.JdbcLiteralFormatterTemporal;
import org.hibernate.type.spi.TypeConfiguration;
import org.neo4j.jdbc.values.UncoercibleException;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.OffsetTime;

/**
 * Descriptor for {@link SqlTypes#TIME_UTC TIME_UTC} handling.
 *
 * @author Christian Beikov
 */
public class TimeUtcAsOffsetTimeJdbcType implements JdbcType {

	public static final TimeUtcAsOffsetTimeJdbcType INSTANCE = new TimeUtcAsOffsetTimeJdbcType();

	public TimeUtcAsOffsetTimeJdbcType() {
	}

	@Override
	public int getJdbcTypeCode() {
		return Types.TIME;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.TIME_UTC;
	}

	@Override
	public String getFriendlyName() {
		return "TIME_UTC";
	}

	@Override
	public String toString() {
		return "TimeUtcDescriptor";
	}

	@Override
	public JavaType<?> getRecommendedJavaType(
			Integer length,
			Integer scale,
			TypeConfiguration typeConfiguration) {
		return typeConfiguration.getJavaTypeRegistry().resolveDescriptor( OffsetTime.class );
	}

	@Override
	public Class<?> getPreferredJavaTypeClass(WrapperOptions options) {
		return Time.class;
	}

	@Override
	public <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaType<T> javaType) {
		return new JdbcLiteralFormatterTemporal<>( javaType, TemporalType.TIME );
	}

	@Override
	public <X> ValueBinder<X> getBinder(final JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
				st.setObject( index, getBindValue( value, options ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setObject( name, getBindValue( value, options ) );
			}

			@Override
			public OffsetTime getBindValue(X value, WrapperOptions options) throws SQLException {
				return getJavaType().unwrap( value, OffsetTime.class, options );
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(final JavaType<X> javaType) {
		return new BasicExtractor<>( javaType, this ) {

			@Override
			protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
				final OffsetTime time;
				try {
					time = rs.getObject( paramIndex, OffsetTime.class );
				}
				catch (UncoercibleException ex) {
					if ( rs.wasNull() ) {
						return null;
					}
					throw ex;
				}
				return javaType.wrap( time, options );
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
				final OffsetTime time;
				try {
					time = statement.getObject( index, OffsetTime.class );
				}
				catch (UncoercibleException ex) {
					if ( statement.wasNull() ) {
						return null;
					}
					throw ex;
				}
				return javaType.wrap( time, options );
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
				final OffsetTime time;
					try {
						time = statement.getObject( name, OffsetTime.class );
					}
					catch (UncoercibleException ex) {
						if ( statement.wasNull() ) {
							return null;
						}
						throw ex;
					}
				return javaType.wrap( time, options );
			}
		};
	}
}
