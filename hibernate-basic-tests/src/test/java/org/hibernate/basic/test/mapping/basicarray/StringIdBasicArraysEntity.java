/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basicarray;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StringIdBasicArraysEntity extends MappedSuperclassOfBasicArrays<String> {

	public static final MappedSuperclassOfBasicArraysFactory<String, StringIdBasicArraysEntity> FACTORY = new MappedSuperclassOfBasicArraysFactory<>() {

		@Override
		public Class<StringIdBasicArraysEntity> getEntityClass() {
			return StringIdBasicArraysEntity.class;
		}

		@Override
		public StringIdBasicArraysEntity createEmptyEntity() {
			return new StringIdBasicArraysEntity("1");
		}

		@Override
		public StringIdBasicArraysEntity createMinimumEntity() {
			StringIdBasicArraysEntity entity = new StringIdBasicArraysEntity( "2" );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public StringIdBasicArraysEntity createMaximumEntity() {
			StringIdBasicArraysEntity entity = new StringIdBasicArraysEntity( "3" );
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private String id;

	public StringIdBasicArraysEntity() {
	}

	public StringIdBasicArraysEntity(String id) {
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
