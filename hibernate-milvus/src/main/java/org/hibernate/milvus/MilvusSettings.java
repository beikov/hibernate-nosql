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

	/**
	 * A setting to enable the automatic addition of a vector field for Milvus, in case an entity does not have one.
	 * <p>
	 * Milvus requires that every collection (table) contains at least one vector typed field.
	 * With this setting enabled, entities that don't specify a vector field receive a dummy field.
	 *
	 * @settingDefault {@code false}
	 */
	String ADD_MISSING_VECTOR_FIELD = "hibernate.nosql.milvus.add_missing_vector_field";

}
