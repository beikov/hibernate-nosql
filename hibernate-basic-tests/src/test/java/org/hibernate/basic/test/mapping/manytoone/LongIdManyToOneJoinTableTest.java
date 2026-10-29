/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdManyToOneJoinTableEntity.class, SimpleLongIdEntity.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoins.class, comment = "MANY-TO-ONE association join table is modeled as secondary table, requiring join support")
public class LongIdManyToOneJoinTableTest
		extends AbstractManyToOneTest<Long, LongIdManyToOneJoinTableEntity, SimpleLongIdEntity> {
	public LongIdManyToOneJoinTableTest() {
		super( LongIdManyToOneJoinTableEntity.FACTORY );
	}
}
