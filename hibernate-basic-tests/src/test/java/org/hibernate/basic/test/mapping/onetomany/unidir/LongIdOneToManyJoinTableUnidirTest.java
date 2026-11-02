/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.unidir;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyTest;
import org.hibernate.milvus.MilvusSettings;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.Setting;

@DomainModel(annotatedClasses = { LongIdOneToManyJoinTableUnidirEntity.class, SimpleLongIdEntity.class })
@ServiceRegistry(settings = {
		@Setting(name = MilvusSettings.ADD_MISSING_VECTOR_FIELD, value = "true"),
		@Setting(name = MilvusSettings.JOIN_TABLE_COMPATIBILITY, value = "true")
})
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoinTableCollections.class)
public class LongIdOneToManyJoinTableUnidirTest
		extends AbstractOneToManyTest<Long, LongIdOneToManyJoinTableUnidirEntity, SimpleLongIdEntity> {
	public LongIdOneToManyJoinTableUnidirTest() {
		super( LongIdOneToManyJoinTableUnidirEntity.FACTORY );
	}
}
