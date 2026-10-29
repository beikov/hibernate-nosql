/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.version;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class LongIdVersionEntity extends AbstractVersionEntity<Long> {

	public static final VersionEntityFactory<Long, LongIdVersionEntity> FACTORY = new VersionEntityFactory<>() {
		@Override
		public Class<LongIdVersionEntity> getEntityClass() {
			return LongIdVersionEntity.class;
		}

		@Override
		public LongIdVersionEntity createSimpleEntity() {
			return new LongIdVersionEntity( 1L, "LongIdVersionEntity1" );
		}
	};

	@Id
	private Long id;

	public LongIdVersionEntity() {
	}

	public LongIdVersionEntity(Long id, String name) {
		super( name );
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
