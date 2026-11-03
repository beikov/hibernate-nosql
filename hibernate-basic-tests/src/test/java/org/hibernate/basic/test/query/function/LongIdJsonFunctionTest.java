/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.function;

import org.hibernate.basic.test.entity.json.JsonLongIdEntity;
import org.hibernate.milvus.MilvusSettings;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.Setting;

@DomainModel(annotatedClasses = { JsonLongIdEntity.class })
@ServiceRegistry(settings = @Setting(name = MilvusSettings.ADD_MISSING_VECTOR_FIELD, value = "true"))
@SessionFactory
public class LongIdJsonFunctionTest extends AbstractJsonFunctionTest<Long, JsonLongIdEntity> {
	public LongIdJsonFunctionTest() {
		super( JsonLongIdEntity.FACTORY );
	}
}
