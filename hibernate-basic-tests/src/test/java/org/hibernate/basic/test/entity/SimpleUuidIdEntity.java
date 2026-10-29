/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class SimpleUuidIdEntity extends AbstractSimpleEntity<UUID> {

	public static final SimpleEntityFactory<UUID, SimpleUuidIdEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleUuidIdEntity> getEntityClass() {
			return SimpleUuidIdEntity.class;
		}

		@Override
		public SimpleUuidIdEntity createEntity() {
			return new SimpleUuidIdEntity(UUID.fromString( "12345678-1234-5678-1234-567812345678" ), "SimpleUuidIdEntity1");
		}
	};

	@Id
	private UUID id;

	public SimpleUuidIdEntity() {
	}

	public SimpleUuidIdEntity(UUID id, String name) {
		super(name);
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
