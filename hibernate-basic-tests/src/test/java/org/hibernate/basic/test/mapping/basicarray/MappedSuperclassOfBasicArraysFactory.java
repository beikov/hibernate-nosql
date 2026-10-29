/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basicarray;

public interface MappedSuperclassOfBasicArraysFactory<ID, T extends MappedSuperclassOfBasicArrays<ID>> {

	Class<T> getEntityClass();
	T createEmptyEntity();
	T createMinimumEntity();
	T createMaximumEntity();
}
