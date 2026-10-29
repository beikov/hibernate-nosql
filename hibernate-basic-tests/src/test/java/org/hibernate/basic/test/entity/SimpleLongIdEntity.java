/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SimpleLongIdEntity extends AbstractSimpleEntity<Long> {

	public static final SimpleEntityFactory<Long, SimpleLongIdEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleLongIdEntity> getEntityClass() {
			return SimpleLongIdEntity.class;
		}

		@Override
		public SimpleLongIdEntity createEntity() {
			return new SimpleLongIdEntity(1L, "SimpleLongEntity1");
		}
	};

	@Id
	private Long id;

	public SimpleLongIdEntity() {
	}

	public SimpleLongIdEntity(Long id, String name) {
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
