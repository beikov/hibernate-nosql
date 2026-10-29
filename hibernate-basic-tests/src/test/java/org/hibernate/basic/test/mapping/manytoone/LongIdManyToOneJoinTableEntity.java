/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;

@Entity
public class LongIdManyToOneJoinTableEntity extends AbstractManyToOneEntity<Long, SimpleLongIdEntity> {

	public static final ManyToOneEntityFactory<Long, LongIdManyToOneJoinTableEntity> FACTORY = new ManyToOneEntityFactory<>() {
		@Override
		public Class<LongIdManyToOneJoinTableEntity> getEntityClass() {
			return LongIdManyToOneJoinTableEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdManyToOneJoinTableEntity createEmptyEntity() {
			return new LongIdManyToOneJoinTableEntity( 1L );
		}

		@Override
		public LongIdManyToOneJoinTableEntity createSimpleEntity() {
			LongIdManyToOneJoinTableEntity entity = new LongIdManyToOneJoinTableEntity( 2L );
			entity.setTheManyToOne( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@JoinTable
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SimpleLongIdEntity theManyToOne;

	public LongIdManyToOneJoinTableEntity() {
	}

	public LongIdManyToOneJoinTableEntity(Long id) {
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
	public SimpleLongIdEntity getTheManyToOne() {
		return theManyToOne;
	}

	@Override
	public void setTheManyToOne(SimpleLongIdEntity theManyToOne) {
		this.theManyToOne = theManyToOne;
	}
}
