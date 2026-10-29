/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance;

public interface InheritanceEntityFactory<ID, T extends AbstractInheritanceEntity<ID>> {

	Class<T> getEntityClass();
	Class<? extends T> getEntity1Class();
	Class<? extends T> getEntity2Class();
	T createEntity1();
	T createEntity2();
}
