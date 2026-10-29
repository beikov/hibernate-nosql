/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractOneToOneEntity<ID, OTHER extends AbstractSimpleEntity<ID>> {

	public AbstractOneToOneEntity() {
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public abstract OTHER getTheOneToOne();

	public abstract void setTheOneToOne(OTHER theOneToOne);

	public void assertEqual(AbstractOneToOneEntity<ID, OTHER> that) {
		assertEquals( getId(), that.getId() );
		if ( getTheOneToOne() == null || that.getTheOneToOne() == null ) {
			assertEquals( getTheOneToOne(), that.getTheOneToOne() );
		}
		else {
			getTheOneToOne().assertEqual( that.getTheOneToOne() );
		}
	}
}
