/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.any;

import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractAnyTest<ID, T extends AbstractAnyEntity<ID, OTHER>, OTHER extends AbstractSimpleEntity<ID>> {

	private final AnyEntityFactory<ID, T> factory;

	private Object emptyEntityId;
	private Object simpleEntityId;

	public AbstractAnyTest(AnyEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T emptyEntity = factory.createEmptyEntity();
			T simpleEntity = factory.createSimpleEntity();
			em.persist( emptyEntity );
			em.persist( simpleEntity );
			emptyEntityId = emptyEntity.getId();
			simpleEntityId = simpleEntity.getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), emptyEntityId );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), simpleEntityId );
	}

	@AfterEach
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testRead(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), emptyEntityId );
			factory.createEmptyEntity().assertEqual( entity1 );

			T entity2 = em.find( factory.getEntityClass(), simpleEntityId );
			factory.createSimpleEntity().assertEqual( entity2 );
		} );
	}

}
