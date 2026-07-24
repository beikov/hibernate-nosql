/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

public record MilvusParameterValue(int position) implements MilvusTypedValue {
	public MilvusParameterValue {
		assert position > 0;
	}

	public int index() {
		return position - 1;
	}
}
