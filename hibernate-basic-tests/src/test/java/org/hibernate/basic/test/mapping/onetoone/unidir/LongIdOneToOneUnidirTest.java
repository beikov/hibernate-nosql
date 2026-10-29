/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone.unidir;

import org.hibernate.basic.test.entity.SimpleLongIdEntity;
import org.hibernate.basic.test.mapping.onetoone.AbstractOneToOneTest;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdOneToOneUnidirEntity.class, SimpleLongIdEntity.class })
@SessionFactory
public class LongIdOneToOneUnidirTest
		extends AbstractOneToOneTest<Long, LongIdOneToOneUnidirEntity, SimpleLongIdEntity> {
	public LongIdOneToOneUnidirTest() {
		super( LongIdOneToOneUnidirEntity.FACTORY );
	}
}
