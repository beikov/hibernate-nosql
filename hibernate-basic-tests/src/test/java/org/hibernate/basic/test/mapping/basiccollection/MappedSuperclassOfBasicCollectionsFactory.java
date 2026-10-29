/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

public interface MappedSuperclassOfBasicCollectionsFactory<ID, T extends MappedSuperclassOfBasicCollections<ID>> {

	Class<T> getEntityClass();
	T createEmptyEntity();
	T createMinimumEntity();
	T createMaximumEntity();
}
