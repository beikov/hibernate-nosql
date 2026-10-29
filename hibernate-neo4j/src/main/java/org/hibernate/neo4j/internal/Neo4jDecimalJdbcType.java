/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.BasicBinder;
import org.hibernate.type.descriptor.jdbc.DecimalJdbcType;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Descriptor for {@link SqlTypes#DECIMAL DECIMAL} handling.
 *
 * Neo4j only supports FLOAT, but the driver decided to store DECIMAL as a string,
 * so we need to reflect this in the {@link ValueBinder#getBindValue(Object, WrapperOptions)} implementation.
 *
 * @author Christian Beikov
 */
public class Neo4jDecimalJdbcType extends DecimalJdbcType {

	public static final Neo4jDecimalJdbcType INSTANCE = new Neo4jDecimalJdbcType();

	public Neo4jDecimalJdbcType() {
	}

	@Override
	public <X> ValueBinder<X> getBinder(final JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
				st.setBigDecimal( index, javaType.unwrap( value, BigDecimal.class, options ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setBigDecimal( name, javaType.unwrap( value, BigDecimal.class, options ) );
			}

			@Override
			public Object getBindValue(X value, WrapperOptions options) throws SQLException {
				final BigDecimal unwrapped = javaType.unwrap( value, BigDecimal.class, options );
				return unwrapped == null ? null : unwrapped.toString();
			}
		};
	}
}
