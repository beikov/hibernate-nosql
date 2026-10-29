/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.unidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyEntity;
import org.hibernate.basic.test.mapping.onetomany.OneToManyEntityFactory;

import java.util.HashSet;
import java.util.Set;

@Entity
public class LongIdOneToManyJoinTableUnidirEntity extends AbstractOneToManyEntity<Long, SimpleLongIdEntity> {

	public static final OneToManyEntityFactory<Long, LongIdOneToManyJoinTableUnidirEntity> FACTORY = new OneToManyEntityFactory<>() {
		@Override
		public Class<LongIdOneToManyJoinTableUnidirEntity> getEntityClass() {
			return LongIdOneToManyJoinTableUnidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdOneToManyJoinTableUnidirEntity createEmptyEntity() {
			return new LongIdOneToManyJoinTableUnidirEntity( 1L, "entity1" );
		}

		@Override
		public LongIdOneToManyJoinTableUnidirEntity createSimpleEntity() {
			LongIdOneToManyJoinTableUnidirEntity entity = new LongIdOneToManyJoinTableUnidirEntity( 2L, "entity2" );
			entity.getTheOneToMany().add( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@JoinTable
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<SimpleLongIdEntity> theOneToMany = new HashSet<>();

	public LongIdOneToManyJoinTableUnidirEntity() {
	}

	public LongIdOneToManyJoinTableUnidirEntity(Long id, String name) {
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
	public Set<SimpleLongIdEntity> getTheOneToMany() {
		return theOneToMany;
	}

	@Override
	public void setTheOneToMany(Set<SimpleLongIdEntity> theOneToMany) {
		this.theOneToMany = theOneToMany;
	}
}
