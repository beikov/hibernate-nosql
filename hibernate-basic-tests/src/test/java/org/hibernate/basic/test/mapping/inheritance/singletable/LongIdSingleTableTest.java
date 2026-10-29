/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.singletable;

import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceTest;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdSingleTableEntity.class, LongIdSingleTableEntitySub1.class, LongIdSingleTableEntitySub2.class })
@SessionFactory
public class LongIdSingleTableTest extends AbstractInheritanceTest<Long, LongIdSingleTableEntity> {
	public LongIdSingleTableTest() {
		super( LongIdSingleTableEntity.FACTORY );
	}
}
