/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.unidir;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyTest;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdOneToManyUnidirEntity.class, SimpleLongIdEntity.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsPartialUpdate.class, comment = "ONE-TO-MANY management requries a partial update of the FK column in the target table")
public class LongIdOneToManyUnidirTest
		extends AbstractOneToManyTest<Long, LongIdOneToManyUnidirEntity, SimpleLongIdEntity> {
	public LongIdOneToManyUnidirTest() {
		super( LongIdOneToManyUnidirEntity.FACTORY );
	}
}
