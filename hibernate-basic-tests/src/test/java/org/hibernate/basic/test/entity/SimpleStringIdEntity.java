/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SimpleStringIdEntity extends AbstractSimpleEntity<String> {

	public static final SimpleEntityFactory<String, SimpleStringIdEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleStringIdEntity> getEntityClass() {
			return SimpleStringIdEntity.class;
		}

		@Override
		public SimpleStringIdEntity createEntity() {
			return new SimpleStringIdEntity("1", "SimpleStringIdEntity1");
		}
	};

	@Id
	private String id;

	public SimpleStringIdEntity() {
	}

	public SimpleStringIdEntity(String id, String name) {
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
