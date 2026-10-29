/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.unidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyEntity;
import org.hibernate.basic.test.mapping.onetomany.OneToManyEntityFactory;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LongIdOneToManyUnidirEntity extends AbstractOneToManyEntity<Long, SimpleLongIdEntity> {

	public static final OneToManyEntityFactory<Long, LongIdOneToManyUnidirEntity> FACTORY = new OneToManyEntityFactory<>() {
		@Override
		public Class<LongIdOneToManyUnidirEntity> getEntityClass() {
			return LongIdOneToManyUnidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdOneToManyUnidirEntity createEmptyEntity() {
			return new LongIdOneToManyUnidirEntity( 1L, "entity1" );
		}

		@Override
		public LongIdOneToManyUnidirEntity createSimpleEntity() {
			LongIdOneToManyUnidirEntity entity = new LongIdOneToManyUnidirEntity( 2L, "entity2" );
			entity.getTheOneToMany().add( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@JoinColumn
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<SimpleLongIdEntity> theOneToMany = new HashSet<>();

	public LongIdOneToManyUnidirEntity() {
	}

	public LongIdOneToManyUnidirEntity(Long id, String name) {
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

	public Set<SimpleLongIdEntity> getTheOneToMany() {
		return theOneToMany;
	}

	public void setTheOneToMany(Set<SimpleLongIdEntity> theOneToMany) {
		this.theOneToMany = theOneToMany;
	}
}
