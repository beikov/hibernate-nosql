/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.tableperclass;

import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdTablePerClassEntitySub1 extends LongIdTablePerClassEntity {

	private Integer sub1Value;

	public LongIdTablePerClassEntitySub1() {
	}

	public LongIdTablePerClassEntitySub1(Long id, String name, Integer sub1Value) {
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
		LongIdTablePerClassEntitySub1 other = (LongIdTablePerClassEntitySub1) that;
		assertEquals( getSub1Value(), other.getSub1Value() );
	}
}
