/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

public interface MappedSuperclassOfBasicsFactory<ID, T extends MappedSuperclassOfBasics<ID>> {

	Class<T> getEntityClass();
	T createEmptyEntity();
	T createSimpleEntity();
	T createMinimumEntity();
	T createMaximumEntity();
}
