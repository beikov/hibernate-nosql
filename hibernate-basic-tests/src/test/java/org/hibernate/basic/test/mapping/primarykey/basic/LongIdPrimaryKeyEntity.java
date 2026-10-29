/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.basic;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyEntity;

@Entity
public class LongIdPrimaryKeyEntity extends AbstractPrimaryKeyEntity<Long> {

	@Id
	private Long id;

	public LongIdPrimaryKeyEntity() {
	}

	public LongIdPrimaryKeyEntity(Long id, String name) {
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
