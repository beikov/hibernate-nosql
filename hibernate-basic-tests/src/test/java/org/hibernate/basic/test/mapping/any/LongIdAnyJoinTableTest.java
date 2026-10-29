/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdAnyJoinTableEntity.class, SimpleLongIdEntity.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoins.class, comment = "ANY association join table is modeled as secondary table, requiring join support")
public class LongIdAnyJoinTableTest
		extends AbstractAnyTest<Long, LongIdAnyJoinTableEntity, SimpleLongIdEntity> {
	public LongIdAnyJoinTableTest() {
		super( LongIdAnyJoinTableEntity.FACTORY );
	}
}
