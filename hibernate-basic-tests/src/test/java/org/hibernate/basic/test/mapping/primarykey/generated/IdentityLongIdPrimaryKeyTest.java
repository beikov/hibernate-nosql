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

@DomainModel(annotatedClasses = IdentityLongIdPrimaryKeyEntity.class)
@SessionFactory
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsIdentityColumns.class)
public class IdentityLongIdPrimaryKeyTest extends AbstractPrimaryKeyTest<Long, IdentityLongIdPrimaryKeyEntity> {

	public IdentityLongIdPrimaryKeyTest() {
		super( new PrimaryKeyEntityFactory<>() {
			@Override
			public Class<IdentityLongIdPrimaryKeyEntity> getEntityClass() {
				return IdentityLongIdPrimaryKeyEntity.class;
			}

			@Override
			public IdentityLongIdPrimaryKeyEntity createSimpleEntity() {
				return new IdentityLongIdPrimaryKeyEntity( "entity1" );
			}
		} );
	}

}
