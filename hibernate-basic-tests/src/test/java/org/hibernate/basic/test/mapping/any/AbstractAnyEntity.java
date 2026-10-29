/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractAnyEntity<ID, OTHER extends AbstractSimpleEntity<ID>> {

	public AbstractAnyEntity() {
	}

	public abstract ID getId();

	public abstract void setId(ID id);

	public abstract OTHER getTheAny();

	public abstract void setTheAny(OTHER theAny);

	public void assertEqual(AbstractAnyEntity<ID, OTHER> that) {
		assertEquals( getId(), that.getId() );
		if ( getTheAny() == null || that.getTheAny() == null ) {
			assertEquals( getTheAny(), that.getTheAny() );
		}
		else {
			getTheAny().assertEqual( that.getTheAny() );
		}
	}
}
