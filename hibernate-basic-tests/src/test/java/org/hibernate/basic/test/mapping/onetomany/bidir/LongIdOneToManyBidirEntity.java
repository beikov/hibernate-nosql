/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.bidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyEntity;
import org.hibernate.basic.test.mapping.onetomany.OneToManyEntityFactory;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LongIdOneToManyBidirEntity extends AbstractOneToManyEntity<Long, SimpleLongIdOneToManyInverseEntity> {

	public static final OneToManyEntityFactory<Long, LongIdOneToManyBidirEntity> FACTORY = new OneToManyEntityFactory<>() {
		@Override
		public Class<LongIdOneToManyBidirEntity> getEntityClass() {
			return LongIdOneToManyBidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdOneToManyInverseEntity.class;
		}

		@Override
		public LongIdOneToManyBidirEntity createEmptyEntity() {
			return new LongIdOneToManyBidirEntity( 1L, "entity1" );
		}

		@Override
		public LongIdOneToManyBidirEntity createSimpleEntity() {
			LongIdOneToManyBidirEntity entity = new LongIdOneToManyBidirEntity( 2L, "entity2" );
			SimpleLongIdOneToManyInverseEntity simpleEntity = SimpleLongIdOneToManyInverseEntity.FACTORY.createEntity();
			entity.getTheOneToMany().add( simpleEntity );
			simpleEntity.setTheManyToOne( entity );
			return entity;
		}
	};

	@Id
	private Long id;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "theManyToOne")
	private Set<SimpleLongIdOneToManyInverseEntity> theOneToMany = new HashSet<>();

	public LongIdOneToManyBidirEntity() {
	}

	public LongIdOneToManyBidirEntity(Long id, String name) {
		super( name );
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

	public Set<SimpleLongIdOneToManyInverseEntity> getTheOneToMany() {
		return theOneToMany;
	}

	public void setTheOneToMany(Set<SimpleLongIdOneToManyInverseEntity> theOneToMany) {
		this.theOneToMany = theOneToMany;
	}
}
