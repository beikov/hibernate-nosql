/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone.unidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetoone.AbstractOneToOneEntity;
import org.hibernate.basic.test.mapping.onetoone.OneToOneEntityFactory;

@Entity
public class LongIdOneToOneJoinTableUnidirEntity extends AbstractOneToOneEntity<Long, SimpleLongIdEntity> {

	public static final OneToOneEntityFactory<Long, LongIdOneToOneJoinTableUnidirEntity> FACTORY = new OneToOneEntityFactory<>() {
		@Override
		public Class<LongIdOneToOneJoinTableUnidirEntity> getEntityClass() {
			return LongIdOneToOneJoinTableUnidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public LongIdOneToOneJoinTableUnidirEntity createEmptyEntity() {
			return new LongIdOneToOneJoinTableUnidirEntity( 1L );
		}

		@Override
		public LongIdOneToOneJoinTableUnidirEntity createSimpleEntity() {
			LongIdOneToOneJoinTableUnidirEntity entity = new LongIdOneToOneJoinTableUnidirEntity( 2L );
			entity.setTheOneToOne( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@JoinTable
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SimpleLongIdEntity theOneToOne;

	public LongIdOneToOneJoinTableUnidirEntity() {
	}

	public LongIdOneToOneJoinTableUnidirEntity(Long id) {
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
	public SimpleLongIdEntity getTheOneToOne() {
		return theOneToOne;
	}

	@Override
	public void setTheOneToOne(SimpleLongIdEntity theOneToOne) {
		this.theOneToOne = theOneToOne;
	}
}
