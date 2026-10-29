/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity;

import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class AbstractSimpleEntity<ID> {

	private String name;

	public AbstractSimpleEntity() {
	}

	public AbstractSimpleEntity(String name) {
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

	public void assertEqual(AbstractSimpleEntity<ID> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getName(), that.getName() );
	}

	@Override
	public final boolean equals(Object o) {
		if ( !(o instanceof AbstractSimpleEntity<?> that) ) {
			return false;
		}

		return Objects.equals( getId(), that.getId() );
	}

	@Override
	public int hashCode() {
		return Objects.hashCode( getId() );
	}
}
