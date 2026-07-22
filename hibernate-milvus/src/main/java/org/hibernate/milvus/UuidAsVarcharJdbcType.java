/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.hibernate.type.spi.TypeConfiguration;

import java.sql.Types;
import java.util.UUID;

import static org.hibernate.type.SqlTypes.UUID;

public class UuidAsVarcharJdbcType extends VarcharJdbcType {

	public static final UuidAsVarcharJdbcType INSTANCE = new UuidAsVarcharJdbcType();

	@Override
	public int getDdlTypeCode() {
		return Types.VARCHAR;
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return UUID;
	}

	@Override
	public JavaType<?> getRecommendedJavaType(Integer length, Integer scale, TypeConfiguration typeConfiguration) {
		return typeConfiguration.getJavaTypeRegistry().resolveDescriptor( UUID.class );
	}
}
