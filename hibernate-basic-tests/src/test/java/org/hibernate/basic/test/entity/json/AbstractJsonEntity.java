/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.entity.json;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MappedSuperclass
public abstract class AbstractJsonEntity<ID> {

	private String name;
	@JdbcTypeCode(SqlTypes.JSON)
	private String theJson;

	public AbstractJsonEntity() {
	}

	public AbstractJsonEntity(String name) {
		this.name = name;
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTheJson() {
		return theJson;
	}

	public void setTheJson(String theJson) {
		this.theJson = theJson;
	}

	public void assertEqual(AbstractJsonEntity<ID> that) {
		assertEquals( getId(), that.getId() );
		assertEquals( getName(), that.getName() );
		assertEquals( getTheJson(), that.getTheJson() );
	}

	@Override
	public final boolean equals(Object o) {
		if ( !(o instanceof AbstractJsonEntity<?> that) ) {
			return false;
		}

		return Objects.equals( getId(), that.getId() );
	}

	@Override
	public int hashCode() {
		return Objects.hashCode( getId() );
	}
}
