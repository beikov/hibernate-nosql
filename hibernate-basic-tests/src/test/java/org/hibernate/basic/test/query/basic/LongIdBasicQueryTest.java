/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.basic;

import org.hibernate.basic.test.mapping.basic.LongIdBasicEntity;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdBasicEntity.class })
@SessionFactory
public class LongIdBasicQueryTest extends AbstractBasicQueryTest<Long, LongIdBasicEntity> {
	public LongIdBasicQueryTest() {
		super( LongIdBasicEntity.FACTORY );
	}
}
