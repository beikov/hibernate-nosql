/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;

public interface AnyEntityFactory<ID, T extends AbstractAnyEntity<ID, ? extends AbstractSimpleEntity<ID>>> {

	Class<T> getEntityClass();
	T createEmptyEntity();
	T createSimpleEntity();
}
