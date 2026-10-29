/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.joined;

import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceTest;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdJoinedEntity.class, LongIdJoinedEntitySub1.class, LongIdJoinedEntitySub2.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoins.class)
public class LongIdJoinedTest extends AbstractInheritanceTest<Long, LongIdJoinedEntity> {
	public LongIdJoinedTest() {
		super( LongIdJoinedEntity.FACTORY );
	}
}
