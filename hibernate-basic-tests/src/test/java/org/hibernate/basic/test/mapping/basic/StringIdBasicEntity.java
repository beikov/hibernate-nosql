/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class StringIdBasicEntity extends MappedSuperclassOfBasics<String> {

	public static final MappedSuperclassOfBasicsFactory<String, StringIdBasicEntity> FACTORY = new MappedSuperclassOfBasicsFactory<>() {

		@Override
		public Class<StringIdBasicEntity> getEntityClass() {
			return StringIdBasicEntity.class;
		}

		@Override
		public StringIdBasicEntity createEmptyEntity() {
			return new StringIdBasicEntity("1");
		}

		@Override
		public StringIdBasicEntity createSimpleEntity() {
			StringIdBasicEntity entity = new StringIdBasicEntity( "2" );
			entity.setSimpleValues();
			return entity;
		}

		@Override
		public StringIdBasicEntity createMinimumEntity() {
			StringIdBasicEntity entity = new StringIdBasicEntity( "3" );
			entity.setMinimumValues();
			return entity;
		}

		@Override
		public StringIdBasicEntity createMaximumEntity() {
			StringIdBasicEntity entity = new StringIdBasicEntity( "4" );
			entity.setMaximumValues();
			return entity;
		}
	};

	@Id
	private String id;

	public StringIdBasicEntity() {
	}

	public StringIdBasicEntity(String id) {
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
