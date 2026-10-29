/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany.unidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.manytomany.AbstractManyToManyEntity;
import org.hibernate.basic.test.mapping.manytomany.ManyToManyEntityFactory;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LongIdManyToManyUnidirEntity extends AbstractManyToManyEntity<Long, SimpleLongIdEntity> {

	public static final ManyToManyEntityFactory<Long, LongIdManyToManyUnidirEntity> FACTORY = new ManyToManyEntityFactory<>() {
		@Override
		public Class<LongIdManyToManyUnidirEntity> getEntityClass() {
			return LongIdManyToManyUnidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdManyToManyUnidirEntity createEmptyEntity() {
			return new LongIdManyToManyUnidirEntity( 1L, "entity1" );
		}

		@Override
		public LongIdManyToManyUnidirEntity createSimpleEntity() {
			LongIdManyToManyUnidirEntity entity = new LongIdManyToManyUnidirEntity( 2L, "entity2" );
			entity.getTheManyToMany().add( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<SimpleLongIdEntity> theManyToMany = new HashSet<>();

	public LongIdManyToManyUnidirEntity() {
	}

	public LongIdManyToManyUnidirEntity(Long id, String name) {
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
	public Set<SimpleLongIdEntity> getTheManyToMany() {
		return theManyToMany;
	}

	@Override
	public void setTheManyToMany(Set<SimpleLongIdEntity> theOneToMany) {
		this.theManyToMany = theOneToMany;
	}
}
