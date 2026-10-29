/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany.bidir;

import org.hibernate.basic.test.mapping.manytomany.AbstractManyToManyTest;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdManyToManyBidirEntity.class, SimpleLongIdManyToManyInverseEntity.class })
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoinTableCollections.class)
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsCompositePrimaryKey.class, comment = "MANY-TO-MANY join table uses a composite primary key")
public class LongIdManyToManyBidirTest
		extends AbstractManyToManyTest<Long, LongIdManyToManyBidirEntity, SimpleLongIdManyToManyInverseEntity> {
	public LongIdManyToManyBidirTest() {
		super( LongIdManyToManyBidirEntity.FACTORY );
	}
}
