/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.joined;

import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdJoinedEntitySub1 extends LongIdJoinedEntity {

	private Integer sub1Value;

	public LongIdJoinedEntitySub1() {
	}

	public LongIdJoinedEntitySub1(Long id, String name, Integer sub1Value) {
		super( id, name );
		this.sub1Value = sub1Value;
	}

	public Integer getSub1Value() {
		return sub1Value;
	}

	public void setSub1Value(Integer sub1Value) {
		this.sub1Value = sub1Value;
	}

	@Override
	public void assertEqual(AbstractInheritanceEntity<Long> that) {
		super.assertEqual( that );
		LongIdJoinedEntitySub1 other = (LongIdJoinedEntitySub1) that;
		assertEquals( getSub1Value(), other.getSub1Value() );
	}
}
