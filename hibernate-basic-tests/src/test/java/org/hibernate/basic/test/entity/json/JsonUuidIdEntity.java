/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class JsonUuidIdEntity extends AbstractJsonEntity<UUID> {

	public static final JsonEntityFactory<UUID, JsonUuidIdEntity> FACTORY = new JsonEntityFactory<>() {
		@Override
		public Class<JsonUuidIdEntity> getEntityClass() {
			return JsonUuidIdEntity.class;
		}

		@Override
		public JsonUuidIdEntity createEntity() {
			return new JsonUuidIdEntity(UUID.fromString( "12345678-1234-5678-1234-567812345678" ), "SimpleUuidIdEntity1");
		}
	};

	@Id
	private UUID id;

	public JsonUuidIdEntity() {
	}

	public JsonUuidIdEntity(UUID id, String name) {
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
