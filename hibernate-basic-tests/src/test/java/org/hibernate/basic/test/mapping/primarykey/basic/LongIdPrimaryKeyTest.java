/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.basic;

import org.hibernate.basic.test.mapping.primarykey.AbstractPrimaryKeyTest;
import org.hibernate.basic.test.mapping.primarykey.PrimaryKeyEntityFactory;
import org.hibernate.milvus.MilvusSettings;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.Setting;

@DomainModel(annotatedClasses = LongIdPrimaryKeyEntity.class)
@ServiceRegistry(settings = @Setting(name = MilvusSettings.ADD_MISSING_VECTOR_FIELD, value = "true"))
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
