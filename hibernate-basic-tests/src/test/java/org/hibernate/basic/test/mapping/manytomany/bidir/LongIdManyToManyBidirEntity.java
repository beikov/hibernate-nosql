/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany.bidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import org.hibernate.basic.test.mapping.manytomany.AbstractManyToManyEntity;
import org.hibernate.basic.test.mapping.manytomany.ManyToManyEntityFactory;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LongIdManyToManyBidirEntity extends AbstractManyToManyEntity<Long, SimpleLongIdManyToManyInverseEntity> {

	public static final ManyToManyEntityFactory<Long, LongIdManyToManyBidirEntity> FACTORY = new ManyToManyEntityFactory<>() {
		@Override
		public Class<LongIdManyToManyBidirEntity> getEntityClass() {
			return LongIdManyToManyBidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdManyToManyInverseEntity.class;
		}

		@Override
		public LongIdManyToManyBidirEntity createEmptyEntity() {
			return new LongIdManyToManyBidirEntity( 1L, "entity1" );
		}

		@Override
		public LongIdManyToManyBidirEntity createSimpleEntity() {
			LongIdManyToManyBidirEntity entity = new LongIdManyToManyBidirEntity( 2L, "entity2" );
			SimpleLongIdManyToManyInverseEntity simpleEntity = SimpleLongIdManyToManyInverseEntity.FACTORY.createEntity();
			entity.getTheManyToMany().add( simpleEntity );
			simpleEntity.getTheManyToMany().add( entity );
			return entity;
		}
	};

	@Id
	private Long id;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(joinColumns = @JoinColumn(name = "bidir_id"), inverseJoinColumns = @JoinColumn(name = "inverse_id"))
	private Set<SimpleLongIdManyToManyInverseEntity> theManyToMany = new HashSet<>();

	public LongIdManyToManyBidirEntity() {
	}

	public LongIdManyToManyBidirEntity(Long id, String name) {
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

	@Override
	public Set<SimpleLongIdManyToManyInverseEntity> getTheManyToMany() {
		return theManyToMany;
	}

	@Override
	public void setTheManyToMany(Set<SimpleLongIdManyToManyInverseEntity> theOneToMany) {
		this.theManyToMany = theOneToMany;
	}
}
