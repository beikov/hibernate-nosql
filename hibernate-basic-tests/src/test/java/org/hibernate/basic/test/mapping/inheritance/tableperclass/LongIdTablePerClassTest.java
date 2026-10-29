/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.tableperclass;

import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceTest;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

@DomainModel(annotatedClasses = { LongIdTablePerClassEntity.class, LongIdTablePerClassEntitySub1.class, LongIdTablePerClassEntitySub2.class })
@SessionFactory
public class LongIdTablePerClassTest extends AbstractInheritanceTest<Long, LongIdTablePerClassEntity> {

	public LongIdTablePerClassTest() {
		super( LongIdTablePerClassEntity.FACTORY );
	}

	@Test
	@Override
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsUnion.class)
	public void testReadBase(SessionFactoryScope scope) {
		super.testReadBase( scope );
	}
}
