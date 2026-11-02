/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.milvus.MilvusSettings;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.Setting;

@DomainModel(annotatedClasses = { LongIdManyToOneEntity.class, SimpleLongIdEntity.class })
@ServiceRegistry(settings = @Setting(name = MilvusSettings.ADD_MISSING_VECTOR_FIELD, value = "true"))
@SessionFactory
public class LongIdManyToOneTest extends AbstractManyToOneTest<Long, LongIdManyToOneEntity, SimpleLongIdEntity> {
	public LongIdManyToOneTest() {
		super( LongIdManyToOneEntity.FACTORY );
	}
}
