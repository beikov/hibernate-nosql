/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity.json;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class JsonStringIdEntity extends AbstractJsonEntity<String> {

	public static final JsonEntityFactory<String, JsonStringIdEntity> FACTORY = new JsonEntityFactory<>() {
		@Override
		public Class<JsonStringIdEntity> getEntityClass() {
			return JsonStringIdEntity.class;
		}

		@Override
		public JsonStringIdEntity createEntity() {
			return new JsonStringIdEntity("1", "SimpleStringIdEntity1");
		}
	};

	@Id
	private String id;

	public JsonStringIdEntity() {
	}

	public JsonStringIdEntity(String id, String name) {
		super(name);
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
