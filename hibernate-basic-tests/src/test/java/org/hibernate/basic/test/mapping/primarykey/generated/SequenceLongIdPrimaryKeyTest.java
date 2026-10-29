/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.generated;

import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyTest;
import org.hibernate.basic.test.mapping.primarykey.PrimaryKeyEntityFactory;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = SequenceLongIdPrimaryKeyEntity.class)
@SessionFactory
@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsTableSequences.class)
public class SequenceLongIdPrimaryKeyTest extends AbstractPrimaryKeyTest<Long, TableLongIdPrimaryKeyEntity> {

	public SequenceLongIdPrimaryKeyTest() {
		super( new PrimaryKeyEntityFactory<>() {
			@Override
			public Class<TableLongIdPrimaryKeyEntity> getEntityClass() {
				return TableLongIdPrimaryKeyEntity.class;
			}

			@Override
			public TableLongIdPrimaryKeyEntity createSimpleEntity() {
				return new TableLongIdPrimaryKeyEntity( "entity1" );
			}
		} );
	}

}
