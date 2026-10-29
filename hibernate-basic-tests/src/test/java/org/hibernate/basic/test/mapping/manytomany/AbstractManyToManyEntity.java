/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;

import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@MappedSuperclass
public abstract class AbstractManyToManyEntity<ID, OTHER extends AbstractSimpleEntity<ID>> {

	private String name;

	public AbstractManyToManyEntity() {
	}

	public AbstractManyToManyEntity(String name) {
		this.name = name;
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public abstract Set<OTHER> getTheManyToMany();

	public abstract void setTheManyToMany(Set<OTHER> theManyToMany);

	public void assertEqual(AbstractManyToManyEntity<ID, OTHER> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getTheManyToMany().size(), that.getTheManyToMany().size() );
		OUTER: for ( OTHER other1 : getTheManyToMany() ) {
			for ( OTHER other2 : that.getTheManyToMany() ) {
				if ( Objects.equals( other1.getId(), other2.getId() ) ) {
					other1.assertEqual( other2 );
					continue OUTER;
				}
			}
			fail("Couldn't find object with id [" + other1.getId() + "] in set: " + that.getTheManyToMany() );
		}
	}
}
