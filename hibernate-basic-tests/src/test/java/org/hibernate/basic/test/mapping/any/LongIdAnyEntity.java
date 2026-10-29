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
import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.hibernate.annotations.AnyKeyJavaClass;
import org.hibernate.basic.test.entity.SimpleLongIdEntity;

@Entity
public class LongIdAnyEntity extends AbstractAnyEntity<Long, SimpleLongIdEntity> {

	public static final AnyEntityFactory<Long, LongIdAnyEntity> FACTORY = new AnyEntityFactory<>() {
		@Override
		public Class<LongIdAnyEntity> getEntityClass() {
			return LongIdAnyEntity.class;
		}

		@Override
		public LongIdAnyEntity createEmptyEntity() {
			return new LongIdAnyEntity( 1L );
		}

		@Override
		public LongIdAnyEntity createSimpleEntity() {
			LongIdAnyEntity entity = new LongIdAnyEntity( 2L );
			entity.setTheAny( SimpleLongIdEntity.FACTORY.createEntity() );
			return entity;
		}
	};

	@Id
	private Long id;
	@Any(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn
	@AnyKeyJavaClass(Long.class)
	@AnyDiscriminatorImplicitValues
	private SimpleLongIdEntity theAny;

	public LongIdAnyEntity() {
	}

	public LongIdAnyEntity(Long id) {
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

	public SimpleLongIdEntity getTheAny() {
		return theAny;
	}

	public void setTheAny(SimpleLongIdEntity theAny) {
		this.theAny = theAny;
	}
}
