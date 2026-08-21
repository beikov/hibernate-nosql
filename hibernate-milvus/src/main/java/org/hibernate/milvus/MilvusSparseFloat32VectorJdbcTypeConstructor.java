/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.tool.schema.extract.spi.ColumnTypeInformation;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeConstructor;
import org.hibernate.type.spi.TypeConfiguration;

public class MilvusSparseFloat32VectorJdbcTypeConstructor implements JdbcTypeConstructor {

	@Override
	public JdbcType resolveType(
			TypeConfiguration typeConfiguration,
			Dialect dialect,
			JdbcType elementType,
			ColumnTypeInformation columnTypeInformation) {
		final Column column = (Column) columnTypeInformation;
		return new MilvusSparseFloat32VectorJdbcType(
				typeConfiguration.getJdbcTypeRegistry().getDescriptor( SqlTypes.FLOAT ),
				column.getArrayLength()
		);
	}

	@Override
	public int getDefaultSqlTypeCode() {
		return SqlTypes.SPARSE_VECTOR_FLOAT32;
	}
}
