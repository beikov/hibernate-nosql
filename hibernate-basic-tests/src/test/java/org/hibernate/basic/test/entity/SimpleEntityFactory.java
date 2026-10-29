/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity;

public interface SimpleEntityFactory<ID, T extends AbstractSimpleEntity<ID>> {

	Class<T> getEntityClass();
	T createEntity();
}
