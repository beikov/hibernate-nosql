/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.generated;

import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyTest;
import org.hibernate.basic.test.mapping.primarykey.PrimaryKeyEntityFactory;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = SequenceLongIdPrimaryKeyEntity.class)
@SessionFactory
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsSequences.class)
public class TableLongIdPrimaryKeyTest extends AbstractPrimaryKeyTest<Long, SequenceLongIdPrimaryKeyEntity> {

	public TableLongIdPrimaryKeyTest() {
		super( new PrimaryKeyEntityFactory<>() {
			@Override
			public Class<SequenceLongIdPrimaryKeyEntity> getEntityClass() {
				return SequenceLongIdPrimaryKeyEntity.class;
			}

			@Override
			public SequenceLongIdPrimaryKeyEntity createSimpleEntity() {
				return new SequenceLongIdPrimaryKeyEntity( "entity1" );
			}
		} );
	}

}
