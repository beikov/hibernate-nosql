/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.version;

public interface VersionEntityFactory<ID, T extends AbstractVersionEntity<ID>> {

	Class<T> getEntityClass();
	T createSimpleEntity();
}
