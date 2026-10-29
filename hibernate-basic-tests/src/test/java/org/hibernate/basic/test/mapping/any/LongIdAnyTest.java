/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdAnyEntity.class, SimpleLongIdEntity.class })
@SessionFactory
public class LongIdAnyTest extends AbstractAnyTest<Long, LongIdAnyEntity, SimpleLongIdEntity> {
	public LongIdAnyTest() {
		super( LongIdAnyEntity.FACTORY );
	}
}
