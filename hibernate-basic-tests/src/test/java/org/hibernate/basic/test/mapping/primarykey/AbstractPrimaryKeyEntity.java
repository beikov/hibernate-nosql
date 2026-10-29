/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey;

import jakarta.persistence.MappedSuperclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class AbstractPrimaryKeyEntity<ID> {

	private String name;

	public AbstractPrimaryKeyEntity() {
	}

	public AbstractPrimaryKeyEntity(String name) {
		this.name = name;
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void assertEqual(AbstractPrimaryKeyEntity<? extends ID> entity1) {
		assertEquals( getName(), entity1.getName() );
	}
}
