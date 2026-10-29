/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;

public interface OneToOneEntityFactory<ID, T extends AbstractOneToOneEntity<ID, ? extends AbstractSimpleEntity<ID>>> {

	Class<T> getEntityClass();
	Class<?> getElementEntityClass();
	T createEmptyEntity();
	T createSimpleEntity();
}
