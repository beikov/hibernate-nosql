/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.composite;

import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyTest;
import org.hibernate.basic.test.mapping.primarykey.PrimaryKeyEntityFactory;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = CompositeLongIdPrimaryKeyEntity.class)
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsCompositePrimaryKey.class)
public class CompositeLongIdPrimaryKeyTest
		extends AbstractPrimaryKeyTest<CompositeLongIdPrimaryKeyEntityId, CompositeLongIdPrimaryKeyEntity> {

	public CompositeLongIdPrimaryKeyTest() {
		super( new PrimaryKeyEntityFactory<>() {
			@Override
			public Class<CompositeLongIdPrimaryKeyEntity> getEntityClass() {
				return CompositeLongIdPrimaryKeyEntity.class;
			}

			@Override
			public CompositeLongIdPrimaryKeyEntity createSimpleEntity() {
				return new CompositeLongIdPrimaryKeyEntity( 1L, "entity1" );
			}
		} );
	}

}
