/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.bidir;

import org.hibernate.basic.test.mapping.onetomany.AbstractOneToManyTest;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdOneToManyBidirEntity.class, SimpleLongIdOneToManyInverseEntity.class })
@SessionFactory
public class LongIdOneToManyBidirTest
		extends AbstractOneToManyTest<Long, LongIdOneToManyBidirEntity, SimpleLongIdOneToManyInverseEntity> {
	public LongIdOneToManyBidirTest() {
		super( LongIdOneToManyBidirEntity.FACTORY );
	}
}
