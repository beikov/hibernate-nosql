/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

/**
 * Settings for Hibernate NoSQL Milvus configuration.
 */
public interface MilvusSettings {

	/**
	 * A setting to allow the usage of join tables with Milvus.
	 * <p>
	 * Milvus does not support joins and hence, dealing with join tables poses a problem,
	 * since that usually requires joins.
	 * With this compatibility mode enabled, associations are set to be fetched through separate select statements,
	 * allowing to use domain models with join tables at the cost of additional database queries.
	 *
	 * @settingDefault {@code false}
	 */
	String JOIN_TABLE_COMPATIBILITY = "hibernate.nosql.milvus.join_table_compatibility";

}
