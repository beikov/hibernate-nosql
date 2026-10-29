/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.tableperclass;

import jakarta.persistence.Entity;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdTablePerClassEntitySub2 extends LongIdTablePerClassEntity {

	private String subName;

	public LongIdTablePerClassEntitySub2() {
	}

	public LongIdTablePerClassEntitySub2(Long id, String name, String subName) {
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
		LongIdTablePerClassEntitySub2 other = (LongIdTablePerClassEntitySub2) that;
		assertEquals( getSubName(), other.getSubName() );
	}
}
