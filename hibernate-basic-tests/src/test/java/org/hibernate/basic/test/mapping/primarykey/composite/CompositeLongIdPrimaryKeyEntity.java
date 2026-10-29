/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.composite;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyEntity;

@Entity
public class CompositeLongIdPrimaryKeyEntity
		extends AbstractPrimaryKeyEntity<CompositeLongIdPrimaryKeyEntityId> {

	@EmbeddedId
	private CompositeLongIdPrimaryKeyEntityId id;

	public CompositeLongIdPrimaryKeyEntity() {
	}

	public CompositeLongIdPrimaryKeyEntity(Long id, String name) {
		super( name );
		this.id = new CompositeLongIdPrimaryKeyEntityId( id, id );
	}

	@Override
	public CompositeLongIdPrimaryKeyEntityId getId() {
		return id;
	}

	@Override
	public void setId(CompositeLongIdPrimaryKeyEntityId id) {
		this.id = id;
	}
}
