/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany.bidir;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.basic.test.entity.SimpleEntityFactory;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Entity
public class SimpleLongIdManyToManyInverseEntity extends AbstractSimpleEntity<Long> {

	public static final SimpleEntityFactory<Long, SimpleLongIdManyToManyInverseEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleLongIdManyToManyInverseEntity> getEntityClass() {
			return SimpleLongIdManyToManyInverseEntity.class;
		}

		@Override
		public SimpleLongIdManyToManyInverseEntity createEntity() {
			return new SimpleLongIdManyToManyInverseEntity(1L, "SimpleLongEntity1");
		}
	};

	@Id
	private Long id;
	@ManyToMany(mappedBy = "theManyToMany")
	private Set<LongIdManyToManyBidirEntity> theManyToMany = new HashSet<>();

	public SimpleLongIdManyToManyInverseEntity() {
	}

	public SimpleLongIdManyToManyInverseEntity(Long id, String name) {
		super(name);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Set<LongIdManyToManyBidirEntity> getTheManyToMany() {
		return theManyToMany;
	}

	public void setTheManyToMany(Set<LongIdManyToManyBidirEntity> theOneToMany) {
		this.theManyToMany = theOneToMany;
	}

	@Override
	public void assertEqual(AbstractSimpleEntity<Long> that) {
		super.assertEqual( that );
		SimpleLongIdManyToManyInverseEntity other = (SimpleLongIdManyToManyInverseEntity) that;
		assertEquals( getTheManyToMany().size(), other.getTheManyToMany().size() );
		OUTER: for ( LongIdManyToManyBidirEntity entity : getTheManyToMany() ) {
			for ( LongIdManyToManyBidirEntity otherEntity : other.getTheManyToMany() ) {
				if ( entity.getId().equals( otherEntity.getId() ) ) {
					continue OUTER;
				}
			}
			fail("Couldn't find object with id [" + entity.getId() + "] in set: " + other.getTheManyToMany() );
		}
	}
}
