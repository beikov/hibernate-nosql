/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = LongIdBasicEntity.class)
@SessionFactory
public class LongIdBasicTypesTest extends AbstractStandardBasicTypesTest<Long, LongIdBasicEntity> {
	public LongIdBasicTypesTest() {
		super( LongIdBasicEntity.FACTORY );
	}
}
