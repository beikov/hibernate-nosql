/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StringIdBasicCollectionsEntity extends MappedSuperclassOfBasicCollections<String> {

	public static final MappedSuperclassOfBasicCollectionsFactory<String, StringIdBasicCollectionsEntity> FACTORY = new MappedSuperclassOfBasicCollectionsFactory<>() {

		@Override
		public Class<StringIdBasicCollectionsEntity> getEntityClass() {
			return StringIdBasicCollectionsEntity.class;
		}

		@Override
		public StringIdBasicCollectionsEntity createEmptyEntity() {
			return new StringIdBasicCollectionsEntity("1");
		}

		@Override
		public StringIdBasicCollectionsEntity createMinimumEntity() {
			StringIdBasicCollectionsEntity entity = new StringIdBasicCollectionsEntity( "2" );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public StringIdBasicCollectionsEntity createMaximumEntity() {
			StringIdBasicCollectionsEntity entity = new StringIdBasicCollectionsEntity( "3" );
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private String id;

	public StringIdBasicCollectionsEntity() {
	}

	public StringIdBasicCollectionsEntity(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
}
