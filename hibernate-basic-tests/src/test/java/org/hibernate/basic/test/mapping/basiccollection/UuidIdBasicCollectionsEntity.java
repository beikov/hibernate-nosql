/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class UuidIdBasicCollectionsEntity extends MappedSuperclassOfBasicCollections<UUID> {

	public static final MappedSuperclassOfBasicCollectionsFactory<UUID, UuidIdBasicCollectionsEntity> FACTORY = new MappedSuperclassOfBasicCollectionsFactory<>() {
		@Override
		public Class<UuidIdBasicCollectionsEntity> getEntityClass() {
			return UuidIdBasicCollectionsEntity.class;
		}

		@Override
		public UuidIdBasicCollectionsEntity createEmptyEntity() {
			return new UuidIdBasicCollectionsEntity( UUID_1 );
		}

		@Override
		public UuidIdBasicCollectionsEntity createMinimumEntity() {
			final UuidIdBasicCollectionsEntity entity = new UuidIdBasicCollectionsEntity( MIN_UUID_V4 );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public UuidIdBasicCollectionsEntity createMaximumEntity() {
			final UuidIdBasicCollectionsEntity entity = new UuidIdBasicCollectionsEntity( MAX_UUID_V4 );
			entity.setMaximumValues();
			return entity;
		}
	};

	private static final UUID UUID_1 = UUID.fromString( "12345678-1234-5678-1234-567812345678" );

	@Id
	private UUID id;

	public UuidIdBasicCollectionsEntity() {
	}

	public UuidIdBasicCollectionsEntity(UUID id) {
		this.id = id;
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void setId(UUID id) {
		this.id = id;
	}
}
