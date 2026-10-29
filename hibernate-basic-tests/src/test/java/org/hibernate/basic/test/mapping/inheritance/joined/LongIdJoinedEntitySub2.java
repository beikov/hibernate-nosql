/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.joined;

import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdJoinedEntitySub2 extends LongIdJoinedEntity {

	private String subName;

	public LongIdJoinedEntitySub2() {
	}

	public LongIdJoinedEntitySub2(Long id, String name, String subName) {
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
		LongIdJoinedEntitySub2 other = (LongIdJoinedEntitySub2) that;
		assertEquals( getSubName(), other.getSubName() );
	}
}
