/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LongIdBasicCollectionsEntity extends MappedSuperclassOfBasicCollections<Long> {

	public static final MappedSuperclassOfBasicCollectionsFactory<Long, LongIdBasicCollectionsEntity> FACTORY = new MappedSuperclassOfBasicCollectionsFactory<>() {
		@Override
		public Class<LongIdBasicCollectionsEntity> getEntityClass() {
			return LongIdBasicCollectionsEntity.class;
		}

		@Override
		public LongIdBasicCollectionsEntity createEmptyEntity() {
			return new LongIdBasicCollectionsEntity(1L);
		}

		@Override
		public LongIdBasicCollectionsEntity createMinimumEntity() {
			LongIdBasicCollectionsEntity entity = new LongIdBasicCollectionsEntity(2L);
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public LongIdBasicCollectionsEntity createMaximumEntity() {
			LongIdBasicCollectionsEntity entity = new LongIdBasicCollectionsEntity(3L);
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private Long id;

	public LongIdBasicCollectionsEntity() {
	}

	public LongIdBasicCollectionsEntity(Long id) {
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
