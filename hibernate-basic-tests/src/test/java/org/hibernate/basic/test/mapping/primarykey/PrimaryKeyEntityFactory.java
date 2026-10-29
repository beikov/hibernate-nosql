/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey;

public interface PrimaryKeyEntityFactory<ID, T extends AbstractPrimaryKeyEntity<ID>> {

	Class<T> getEntityClass();
	T createSimpleEntity();
}
