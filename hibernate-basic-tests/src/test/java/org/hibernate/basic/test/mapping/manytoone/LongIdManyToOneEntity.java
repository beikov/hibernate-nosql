/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;

@Entity
public class LongIdManyToOneEntity extends AbstractManyToOneEntity<Long, SimpleLongIdEntity> {

	public static final ManyToOneEntityFactory<Long, LongIdManyToOneEntity> FACTORY = new ManyToOneEntityFactory<>() {
		@Override
		public Class<LongIdManyToOneEntity> getEntityClass() {
			return LongIdManyToOneEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdManyToOneEntity createEmptyEntity() {
			return new LongIdManyToOneEntity( 1L );
		}

		@Override
		public LongIdManyToOneEntity createSimpleEntity() {
			LongIdManyToOneEntity entity = new LongIdManyToOneEntity( 2L );
			entity.setTheManyToOne( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SimpleLongIdEntity theManyToOne;

	public LongIdManyToOneEntity() {
	}

	public LongIdManyToOneEntity(Long id) {
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

	public SimpleLongIdEntity getTheManyToOne() {
		return theManyToOne;
	}

	public void setTheManyToOne(SimpleLongIdEntity theManyToOne) {
		this.theManyToOne = theManyToOne;
	}
}
