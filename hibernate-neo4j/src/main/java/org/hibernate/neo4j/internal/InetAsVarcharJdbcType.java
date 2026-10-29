/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.spi.TypeConfiguration;

import java.net.InetAddress;

/**
 * Descriptor for {@link SqlTypes#INET INET} handling.
 *
 * @author Christian Beikov
 */
public class InetAsVarcharJdbcType extends VarcharJdbcType {

	public static final InetAsVarcharJdbcType INSTANCE = new InetAsVarcharJdbcType();

	public InetAsVarcharJdbcType() {
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.INET;
	}

	@Override
	public String getFriendlyName() {
		return "INET";
	}

	@Override
	public String toString() {
		return "InetDescriptor";
	}

	@Override
	public JavaType<?> getRecommendedJavaType(
			Integer length,
			Integer scale,
			TypeConfiguration typeConfiguration) {
		return typeConfiguration.getJavaTypeRegistry().resolveDescriptor( InetAddress.class );
	}
}
