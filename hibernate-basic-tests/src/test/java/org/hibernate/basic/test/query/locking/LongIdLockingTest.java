/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.locking;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { SimpleLongIdEntity.class })
@SessionFactory
public class LongIdLockingTest extends AbstractLockingTest<Long, SimpleLongIdEntity> {
	public LongIdLockingTest() {
		super( SimpleLongIdEntity.FACTORY );
	}
}
