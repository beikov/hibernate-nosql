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
import org.hibernate.type.descriptor.jdbc.VarcharUUIDJdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Descriptor for {@link SqlTypes#INET INET} handling.
 *
 * @author Christian Beikov
 */
public class UuidAsVarcharJdbcType extends VarcharUUIDJdbcType {

	public static final UuidAsVarcharJdbcType INSTANCE = new UuidAsVarcharJdbcType();

	public UuidAsVarcharJdbcType() {
	}

	@Override
	public <X> ValueBinder<X> getBinder(JavaType<X> javaType) {
		return new BasicBinder<>( javaType, this ) {
			@Override
			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
					throws SQLException {
				st.setString( index, getJavaType().unwrap( value, String.class, options ) );
			}

			@Override
			protected void doBind(CallableStatement st, X value, String name, WrapperOptions options)
					throws SQLException {
				st.setString( name, getJavaType().unwrap( value, String.class, options ) );
			}

			@Override
			public Object getBindValue(X value, WrapperOptions options) throws SQLException {
				return getJavaType().unwrap( value, String.class, options );
			}
		};
	}
}
