/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

public sealed interface MilvusRanker
		permits MilvusRRFRanker, MilvusWeightedRanker {
}
