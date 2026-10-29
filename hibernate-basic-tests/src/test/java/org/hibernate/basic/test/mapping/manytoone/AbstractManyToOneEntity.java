/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractManyToOneEntity<ID, OTHER extends AbstractSimpleEntity<ID>> {

	public AbstractManyToOneEntity() {
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public abstract OTHER getTheManyToOne();

	public abstract void setTheManyToOne(OTHER theManyToOne);

	public void assertEqual(AbstractManyToOneEntity<ID, OTHER> that) {
		assertEquals( getId(), that.getId() );
		if ( getTheManyToOne() == null || that.getTheManyToOne() == null ) {
			assertEquals( getTheManyToOne(), that.getTheManyToOne() );
		}
		else {
			getTheManyToOne().assertEqual( that.getTheManyToOne() );
		}
	}
}
