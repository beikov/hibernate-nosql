/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LongIdBasicEntity extends MappedSuperclassOfBasics<Long> {

	public static final MappedSuperclassOfBasicsFactory<Long, LongIdBasicEntity> FACTORY = new MappedSuperclassOfBasicsFactory<>() {
		@Override
		public Class<LongIdBasicEntity> getEntityClass() {
			return LongIdBasicEntity.class;
		}

		@Override
		public LongIdBasicEntity createEmptyEntity() {
			return new LongIdBasicEntity(1L);
		}

		@Override
		public LongIdBasicEntity createSimpleEntity() {
			LongIdBasicEntity entity = new LongIdBasicEntity( 2L );
			entity.setSimpleValues();
			return entity;
		}

		@Override
		public LongIdBasicEntity createMinimumEntity() {
			LongIdBasicEntity entity = new LongIdBasicEntity(3L);
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public LongIdBasicEntity createMaximumEntity() {
			LongIdBasicEntity entity = new LongIdBasicEntity(4L);
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private Long id;

	public LongIdBasicEntity() {
	}

	public LongIdBasicEntity(Long id) {
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
}
