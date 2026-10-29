/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = LongIdBasicCollectionsEntity.class)
@SessionFactory
public class LongIdBasicCollectionsTypesTest
		extends AbstractStandardBasicCollectionsTypesTest<Long, LongIdBasicCollectionsEntity> {
	public LongIdBasicCollectionsTypesTest() {
		super( LongIdBasicCollectionsEntity.FACTORY );
	}
}
