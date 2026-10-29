/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basicarray;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class UuidIdBasicArraysEntity extends MappedSuperclassOfBasicArrays<UUID> {

	public static final MappedSuperclassOfBasicArraysFactory<UUID, UuidIdBasicArraysEntity> FACTORY = new MappedSuperclassOfBasicArraysFactory<>() {
		@Override
		public Class<UuidIdBasicArraysEntity> getEntityClass() {
			return UuidIdBasicArraysEntity.class;
		}

		@Override
		public UuidIdBasicArraysEntity createEmptyEntity() {
			return new UuidIdBasicArraysEntity( UUID_1 );
		}

		@Override
		public UuidIdBasicArraysEntity createMinimumEntity() {
			final UuidIdBasicArraysEntity entity = new UuidIdBasicArraysEntity( MIN_UUID_V4 );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public UuidIdBasicArraysEntity createMaximumEntity() {
			final UuidIdBasicArraysEntity entity = new UuidIdBasicArraysEntity( MAX_UUID_V4 );
			entity.setMaximumValues();
			return entity;
		}
	};

	private static final UUID UUID_1 = UUID.fromString( "12345678-1234-5678-1234-567812345678" );

	@Id
	private UUID id;

	public UuidIdBasicArraysEntity() {
	}

	public UuidIdBasicArraysEntity(UUID id) {
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
