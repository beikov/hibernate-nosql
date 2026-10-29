/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.generated;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyEntity;

@Entity
public class IdentityLongIdPrimaryKeyEntity extends AbstractPrimaryKeyEntity<Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	public IdentityLongIdPrimaryKeyEntity() {
	}

	public IdentityLongIdPrimaryKeyEntity(String name) {
		super( name );
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
