/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.version;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class AbstractVersionEntity<ID> {

	@Version
	private Long version;
	private String name;

	public AbstractVersionEntity() {
	}

	public AbstractVersionEntity(String name) {
		this.name = name;
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void assertEqual(AbstractVersionEntity<ID> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getName(), that.getName() );
	}
}
