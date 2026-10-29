/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey;

import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractPrimaryKeyTest<ID, T extends AbstractPrimaryKeyEntity<ID>> {

	private final PrimaryKeyEntityFactory<ID, T> factory;

	private Object entity1Id;

	public AbstractPrimaryKeyTest(PrimaryKeyEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = factory.createSimpleEntity();
			em.persist( entity1 );
			entity1Id = entity1.getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), entity1Id );
	}

	@AfterEach
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testRead(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), entity1Id );
			factory.createSimpleEntity().assertEqual( entity1 );
		} );
	}

}
