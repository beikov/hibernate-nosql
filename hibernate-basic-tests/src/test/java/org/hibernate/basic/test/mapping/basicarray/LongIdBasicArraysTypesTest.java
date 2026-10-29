/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basicarray;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = LongIdBasicArraysEntity.class)
@SessionFactory
public class LongIdBasicArraysTypesTest
		extends AbstractStandardBasicArraysTypesTest<Long, LongIdBasicArraysEntity> {
	public LongIdBasicArraysTypesTest() {
		super( LongIdBasicArraysEntity.FACTORY );
	}
}
