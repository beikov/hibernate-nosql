/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import org.hibernate.milvus.MilvusSettings;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.Setting;

@DomainModel(annotatedClasses = LongIdBasicEntity.class)
@ServiceRegistry(settings = @Setting(name = MilvusSettings.ADD_MISSING_VECTOR_FIELD, value = "true"))
@SessionFactory
public class LongIdBasicTypesTest extends AbstractStandardBasicTypesTest<Long, LongIdBasicEntity> {
	public LongIdBasicTypesTest() {
		super( LongIdBasicEntity.FACTORY );
	}
}
