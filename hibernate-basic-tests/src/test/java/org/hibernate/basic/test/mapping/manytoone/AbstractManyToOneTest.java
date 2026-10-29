/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytoone;

import jakarta.persistence.criteria.JoinType;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractManyToOneTest<ID, T extends AbstractManyToOneEntity<ID, OTHER>, OTHER extends AbstractSimpleEntity<ID>> {

	private final ManyToOneEntityFactory<ID, T> factory;

	private Object emptyEntityId;
	private Object simpleEntityId;
	private Object elementId;

	public AbstractManyToOneTest(ManyToOneEntityFactory<ID, T> factory) {
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
			elementId = simpleEntity.getTheManyToOne().getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), emptyEntityId );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), simpleEntityId );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getElementEntityClass(), elementId );
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

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoins.class)
	public void testInnerJoinFetch(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			final var join = root.fetch( "theManyToOne", JoinType.INNER );
			cq.select( root );
			final var query = em.createSelectionQuery( cq );
			final var resultList = query.getResultList();

			assertEquals( 1, resultList.size() );
			factory.createSimpleEntity().assertEqual( resultList.get( 0 ) );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsJoins.class)
	public void testLeftJoinFetch(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			final var join = root.fetch( "theManyToOne", JoinType.LEFT );
			cq.select( root );
			final var query = em.createSelectionQuery( cq );
			final var resultList = query.getResultList();

			assertEquals( 2, resultList.size() );
			for ( T entity : resultList ) {
				if ( emptyEntityId.equals( entity.getId() ) ) {
					factory.createEmptyEntity().assertEqual( entity );
				}
				else if ( simpleEntityId.equals( entity.getId() ) ) {
					factory.createSimpleEntity().assertEqual( entity );
				}
				else {
					fail( "Unexpected entity id " + entity.getId() );
				}
			}
		} );
	}

}
