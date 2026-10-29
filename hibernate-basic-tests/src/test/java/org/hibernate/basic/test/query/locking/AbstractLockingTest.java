/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.locking;

import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.PessimisticLockException;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.basic.test.entity.SimpleEntityFactory;
import org.hibernate.jpa.SpecHints;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsSelectLocking.class)
public abstract class AbstractLockingTest<ID, T extends AbstractSimpleEntity<ID>> {

	private final SimpleEntityFactory<ID, T> factory;

	private Object simpleEntityId;

	public AbstractLockingTest(SimpleEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T simpleEntity = factory.createEntity();
			em.persist( simpleEntity );
			simpleEntityId = simpleEntity.getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), simpleEntityId );
	}

	@AfterEach
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testSimpleLock(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), simpleEntityId, LockModeType.PESSIMISTIC_WRITE );
			factory.createEntity().assertEqual( entity1 );
			entity1.setName( "updated" );

			assertLocked( scope, factory.getEntityClass(), simpleEntityId );
		} );
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), simpleEntityId );
			T expectedEntity = factory.createEntity();
			expectedEntity.setName( "updated" );
			expectedEntity.assertEqual( entity1 );
		} );
	}

	@Test
	public void testQueryLock(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			cq.where( cb.equal( root.get( "id" ), simpleEntityId ) );
			cq.select( root );
			final var query = em.createSelectionQuery( cq );
			query.setLockMode( LockModeType.PESSIMISTIC_WRITE );
			T entity1 = query.getSingleResult();
			factory.createEntity().assertEqual( entity1 );
			entity1.setName( "updated" );

			assertLocked( scope, factory.getEntityClass(), simpleEntityId );
		} );
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), simpleEntityId );
			T expectedEntity = factory.createEntity();
			expectedEntity.setName( "updated" );
			expectedEntity.assertEqual( entity1 );
		} );
	}

	private void assertLocked(SessionFactoryScope scope, Class<?> entityClass, Object id) {
		scope.inTransaction( em2 -> {
			try {
				em2.find(
						entityClass,
						id,
						LockModeType.PESSIMISTIC_WRITE,
						Map.of( SpecHints.HINT_SPEC_LOCK_TIMEOUT, 1 )
				);
				fail( "Expected a timeout exception" );
			}
			catch (LockTimeoutException lte) {
				// Proper exception thrown for dialect supporting lock timeouts when an immediate timeout is set.
				lte.getCause();
			}
			catch (PessimisticLockException pe) {
				// Per spec, the Hibernate LockTimeoutException is wrapped into a PessimisticLockException
				// if the transaction was marked for rollback
				if ( !em2.getTransaction().getRollbackOnly()
					 || !(pe.getCause() instanceof org.hibernate.exception.LockTimeoutException) ) {
					fail( "Find with immediate timeout should have thrown LockTimeoutException." );
				}
			}
		});
	}

}
