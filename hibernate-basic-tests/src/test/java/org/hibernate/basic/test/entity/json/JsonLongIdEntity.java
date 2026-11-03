/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class JsonLongIdEntity extends AbstractJsonEntity<Long> {

	public static final JsonEntityFactory<Long, JsonLongIdEntity> FACTORY = new JsonEntityFactory<>() {
		@Override
		public Class<JsonLongIdEntity> getEntityClass() {
			return JsonLongIdEntity.class;
		}

		@Override
		public JsonLongIdEntity createEntity() {
			return new JsonLongIdEntity(1L, "SimpleLongEntity1");
		}
	};

	@Id
	private Long id;

	public JsonLongIdEntity() {
	}

	public JsonLongIdEntity(Long id, String name) {
		super(name);
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
