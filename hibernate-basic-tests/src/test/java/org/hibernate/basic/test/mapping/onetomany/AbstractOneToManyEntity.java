/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@MappedSuperclass
public abstract class AbstractOneToManyEntity<ID, OTHER extends AbstractSimpleEntity<ID>> {

	private String name;

	public AbstractOneToManyEntity() {
	}

	public AbstractOneToManyEntity(String name) {
		this.name = name;
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public abstract Set<OTHER> getTheOneToMany();

	public abstract void setTheOneToMany(Set<OTHER> theOneToMany);

	public void assertEqual(AbstractOneToManyEntity<ID, OTHER> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getTheOneToMany().size(), that.getTheOneToMany().size() );
		OUTER: for ( OTHER other1 : getTheOneToMany() ) {
			for ( OTHER other2 : that.getTheOneToMany() ) {
				if ( Objects.equals( other1.getId(), other2.getId() ) ) {
					assertEquals( other1.getName(), other2.getName() );
					continue OUTER;
				}
			}
			fail("Couldn't find object with id [" + other1.getId() + "] in set: " + that.getTheOneToMany() );
		}
	}
}
