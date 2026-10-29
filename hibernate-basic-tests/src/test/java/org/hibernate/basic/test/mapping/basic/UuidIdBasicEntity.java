/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class UuidIdBasicEntity extends MappedSuperclassOfBasics<UUID> {

	public static final MappedSuperclassOfBasicsFactory<UUID, UuidIdBasicEntity> FACTORY = new MappedSuperclassOfBasicsFactory<>() {
		@Override
		public Class<UuidIdBasicEntity> getEntityClass() {
			return UuidIdBasicEntity.class;
		}

		@Override
		public UuidIdBasicEntity createEmptyEntity() {
			return new UuidIdBasicEntity( UUID_1 );
		}

		@Override
		public UuidIdBasicEntity createSimpleEntity() {
			final UuidIdBasicEntity entity = new UuidIdBasicEntity( UUID_2 );
			entity.setSimpleValues();
			return entity;
		}

		@Override
		public UuidIdBasicEntity createMinimumEntity() {
			final UuidIdBasicEntity entity = new UuidIdBasicEntity( MIN_UUID_V4 );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public UuidIdBasicEntity createMaximumEntity() {
			final UuidIdBasicEntity entity = new UuidIdBasicEntity( MAX_UUID_V4 );
			entity.setMaximumValues();
			return entity;
		}
	};

	private static final UUID UUID_1 = UUID.fromString( "12345678-1234-5678-1234-567812345678" );
	private static final UUID UUID_2 = UUID.fromString( "12345678-9123-4567-8123-456781234567" );

	@Id
	private UUID id;

	public UuidIdBasicEntity() {
	}

	public UuidIdBasicEntity(UUID id) {
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
