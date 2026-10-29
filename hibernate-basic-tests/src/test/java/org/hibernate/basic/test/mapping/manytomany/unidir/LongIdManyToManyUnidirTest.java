/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany.unidir;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.manytomany.AbstractManyToManyTest;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdManyToManyUnidirEntity.class, SimpleLongIdEntity.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoinTableCollections.class)
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsCompositePrimaryKey.class, comment = "MANY-TO-MANY join table uses a composite primary key")
public class LongIdManyToManyUnidirTest
		extends AbstractManyToManyTest<Long, LongIdManyToManyUnidirEntity, SimpleLongIdEntity> {
	public LongIdManyToManyUnidirTest() {
		super( LongIdManyToManyUnidirEntity.FACTORY );
	}
}
