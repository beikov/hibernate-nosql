/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.singletable;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.*;

@Entity
@DiscriminatorValue("BASE")
public class LongIdSingleTableEntitySub1 extends LongIdSingleTableEntity {

	private Integer sub1Value;

	public LongIdSingleTableEntitySub1() {
	}

	public LongIdSingleTableEntitySub1(Long id, String name, Integer sub1Value) {
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
		LongIdSingleTableEntitySub1 other = (LongIdSingleTableEntitySub1) that;
		assertEquals( getSub1Value(), other.getSub1Value() );
	}
}
