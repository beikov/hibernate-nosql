/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.hibernate.annotations.AnyKeyJavaClass;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;

@Entity
public class LongIdAnyJoinTableEntity extends AbstractAnyEntity<Long, SimpleLongIdEntity> {

	public static final AnyEntityFactory<Long, LongIdAnyJoinTableEntity> FACTORY = new AnyEntityFactory<>() {
		@Override
		public Class<LongIdAnyJoinTableEntity> getEntityClass() {
			return LongIdAnyJoinTableEntity.class;
		}

		@Override
		public LongIdAnyJoinTableEntity createEmptyEntity() {
			return new LongIdAnyJoinTableEntity( 1L );
		}

		@Override
		public LongIdAnyJoinTableEntity createSimpleEntity() {
			LongIdAnyJoinTableEntity entity = new LongIdAnyJoinTableEntity( 2L );
			entity.setTheAny( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@JoinTable(name = "the_any_join_table")
	@Any(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn
	@AnyKeyJavaClass(Long.class)
	@AnyDiscriminatorImplicitValues
	private SimpleLongIdEntity theAny;

	public LongIdAnyJoinTableEntity() {
	}

	public LongIdAnyJoinTableEntity(Long id) {
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
	public SimpleLongIdEntity getTheAny() {
		return theAny;
	}

	@Override
	public void setTheAny(SimpleLongIdEntity theAny) {
		this.theAny = theAny;
	}
}
