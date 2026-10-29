/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.basic;

import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyTest;
import org.hibernate.basic.test.mapping.primarykey.PrimaryKeyEntityFactory;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = LongIdPrimaryKeyEntity.class)
@SessionFactory
public class LongIdPrimaryKeyTest extends AbstractPrimaryKeyTest<Long, LongIdPrimaryKeyEntity> {

	public LongIdPrimaryKeyTest() {
		super( new PrimaryKeyEntityFactory<>() {
			@Override
			public Class<LongIdPrimaryKeyEntity> getEntityClass() {
				return LongIdPrimaryKeyEntity.class;
			}

			@Override
			public LongIdPrimaryKeyEntity createSimpleEntity() {
				return new LongIdPrimaryKeyEntity( 1L, "entity1" );
			}
		} );
	}

}
