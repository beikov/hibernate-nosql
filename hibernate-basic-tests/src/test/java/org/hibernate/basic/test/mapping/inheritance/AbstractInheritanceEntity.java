/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance;

import jakarta.persistence.MappedSuperclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class AbstractInheritanceEntity<ID> {

	private String name;

	public AbstractInheritanceEntity() {
	}

	public AbstractInheritanceEntity(String name) {
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

	public void assertEqual(AbstractInheritanceEntity<ID> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getName(), that.getName() );
	}
}
