/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdManyToOneEntity.class, SimpleLongIdEntity.class })
@SessionFactory
public class LongIdManyToOneTest extends AbstractManyToOneTest<Long, LongIdManyToOneEntity, SimpleLongIdEntity> {
	public LongIdManyToOneTest() {
		super( LongIdManyToOneEntity.FACTORY );
	}
}
