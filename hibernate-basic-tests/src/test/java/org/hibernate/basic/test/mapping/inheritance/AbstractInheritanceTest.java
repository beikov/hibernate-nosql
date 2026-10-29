/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance;

import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractInheritanceTest<ID, T extends AbstractInheritanceEntity<ID>> {

	private final InheritanceEntityFactory<ID, T> factory;

	private Object entity1Id;
	private Object entity2Id;

	public AbstractInheritanceTest(InheritanceEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeAll
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = factory.createEntity1();
			T entity2 = factory.createEntity2();
			em.persist( entity1 );
			em.persist( entity2 );
			entity1Id = entity1.getId();
			entity2Id = entity2.getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntity1Class(), entity1Id );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntity2Class(), entity2Id );
	}

	@AfterAll
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testReadBase(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), entity1Id );
			factory.createEntity1().assertEqual( entity1 );
			T entity2 = em.find( factory.getEntityClass(), entity2Id );
			factory.createEntity2().assertEqual( entity2 );
		} );
	}

	@Test
	public void testReadConcrete(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntity1Class(), entity1Id );
			factory.createEntity1().assertEqual( entity1 );
			T entity2 = em.find( factory.getEntity2Class(), entity2Id );
			factory.createEntity2().assertEqual( entity2 );
		} );
	}

	@Test
	public void testReadWrongType(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntity2Class(), entity1Id );
			assertNull( entity1 );
			T entity2 = em.find( factory.getEntity1Class(), entity2Id );
			assertNull( entity2 );
		} );
	}

}
