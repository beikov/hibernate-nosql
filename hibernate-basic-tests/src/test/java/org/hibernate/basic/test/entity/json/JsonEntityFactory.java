/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity.json;

public interface JsonEntityFactory<ID, T extends AbstractJsonEntity<ID>> {

	Class<T> getEntityClass();
	T createEntity();
}
