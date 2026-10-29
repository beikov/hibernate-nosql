/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basicarray;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LongIdBasicArraysEntity extends MappedSuperclassOfBasicArrays<Long> {

	public static final MappedSuperclassOfBasicArraysFactory<Long, LongIdBasicArraysEntity> FACTORY = new MappedSuperclassOfBasicArraysFactory<>() {
		@Override
		public Class<LongIdBasicArraysEntity> getEntityClass() {
			return LongIdBasicArraysEntity.class;
		}

		@Override
		public LongIdBasicArraysEntity createEmptyEntity() {
			return new LongIdBasicArraysEntity(1L);
		}

		@Override
		public LongIdBasicArraysEntity createMinimumEntity() {
			LongIdBasicArraysEntity entity = new LongIdBasicArraysEntity(2L);
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public LongIdBasicArraysEntity createMaximumEntity() {
			LongIdBasicArraysEntity entity = new LongIdBasicArraysEntity(3L);
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private Long id;

	public LongIdBasicArraysEntity() {
	}

	public LongIdBasicArraysEntity(Long id) {
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
