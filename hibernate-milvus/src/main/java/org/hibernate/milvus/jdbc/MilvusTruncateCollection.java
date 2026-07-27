/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

public record MilvusTruncateCollection(
		String collectionName
) implements MilvusStatementDefinition {
	@Override
	public int parameterCount() {
		return 0;
	}
}
