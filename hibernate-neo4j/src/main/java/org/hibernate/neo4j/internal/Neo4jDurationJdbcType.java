/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.BasicExtractor;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.spi.TypeConfiguration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

/**
 * Descriptor for {@link SqlTypes#DURATION DURATION} handling.
 *
 * Neo4j has native duration support, and since Neo4j doesn't support DECIMAL,
 * we prefer the native data type.
 *
 * @author Christian Beikov
 */
public class Neo4jDurationJdbcType implements JdbcType {

	public static final Neo4jDurationJdbcType INSTANCE = new Neo4jDurationJdbcType();

	public Neo4jDurationJdbcType() {
	}

	@Override
	public int getJdbcTypeCode() {
		return SqlTypes.NUMERIC;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.DURATION;
	}

	@Override
	public int getDdlTypeCode() {
		return SqlTypes.DURATION;
	}

	@Override
	public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
					throws SQLException {
				st.setObject( index, getBindValue( value, options ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setObject( name, getBindValue( value, options ) );
			}

			@Override
			public Object getBindValue(X value, WrapperOptions options) throws SQLException {
				return getJavaType().unwrap( value, Duration.class, options );
			}
		};
	}

	@Override
	public <X> ValueExtractor<X> getExtractor(JavaType<X> javaType) {
		return new BasicExtractor<X>( javaType, this ) {
			@Override
			protected X doExtract(ResultSet rs, int paramIndex, WrapperOptions options) throws SQLException {
				Duration duration = null;
				try {
					duration = rs.getObject( paramIndex, Duration.class );
				}
				catch (SQLException e) {
					if ( !"data exception - Cannot coerce NULL (NULL) to java.time.Duration".equals(
							e.getMessage() ) ) {
						throw e;
					}
				}
				return getJavaType().wrap( duration, options );
			}

			@Override
			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
				Duration duration = null;
				try {
					duration = statement.getObject( index, Duration.class );
				}
				catch (SQLException e) {
					if ( !"data exception - Cannot coerce NULL (NULL) to java.time.Duration".equals(
							e.getMessage() ) ) {
						throw e;
					}
				}
				return getJavaType().wrap( duration, options );
			}

			@Override
			protected X doExtract(CallableStatement statement, String name, WrapperOptions options)
					throws SQLException {
				Duration duration = null;
				try {
					duration = statement.getObject( name, Duration.class );
				}
				catch (SQLException e) {
					if ( !"data exception - Cannot coerce NULL (NULL) to java.time.Duration".equals(
							e.getMessage() ) ) {
						throw e;
					}
				}
				return getJavaType().wrap( duration, options );
			}
		};
	}

	@Override
	public JavaType<?> getRecommendedJavaType(Integer precision, Integer scale, TypeConfiguration typeConfiguration) {
		return typeConfiguration.getJavaTypeRegistry().resolveDescriptor( Duration.class );
	}

	@Override
	public Class<?> getPreferredJavaTypeClass(WrapperOptions options) {
		return Duration.class;
	}

	@Override
	public String getFriendlyName() {
		return "DURATION";
	}

	@Override
	public String toString() {
		return "DurationJdbcType";
	}
}
