/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
@DiscriminatorValue("SUB2")
public class LongIdSingleTableEntitySub2 extends LongIdSingleTableEntity {

	private String subName;

	public LongIdSingleTableEntitySub2() {
	}

	public LongIdSingleTableEntitySub2(Long id, String name, String subName) {
		super( id, name );
		this.subName = subName;
	}

	public String getSubName() {
		return subName;
	}

	public void setSubName(String subName) {
		this.subName = subName;
	}

	@Override
	public void assertEqual(AbstractInheritanceEntity<Long> that) {
		super.assertEqual( that );
		LongIdSingleTableEntitySub2 other = (LongIdSingleTableEntitySub2) that;
		assertEquals( getSubName(), other.getSubName() );
	}
}
