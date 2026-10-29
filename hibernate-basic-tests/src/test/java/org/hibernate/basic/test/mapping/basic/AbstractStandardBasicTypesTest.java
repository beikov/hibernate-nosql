/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basic;

import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public abstract class AbstractStandardBasicTypesTest<ID, T extends MappedSuperclassOfBasics<ID>> {

	private final MappedSuperclassOfBasicsFactory<ID, T> factory;

	private Object emptyEntityId;
	private Object minimumEntityId;
	private Object maximumEntityId;

	public AbstractStandardBasicTypesTest(MappedSuperclassOfBasicsFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeAll
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T emptyEntity = factory.createEmptyEntity();
			T minimumEntity = factory.createMinimumEntity();
			T maximumEntity = factory.createMaximumEntity();
			em.persist( emptyEntity );
			em.persist( minimumEntity );
			em.persist( maximumEntity );
			emptyEntityId = emptyEntity.getId();
			minimumEntityId = minimumEntity.getId();
			maximumEntityId = maximumEntity.getId();
		} );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), emptyEntityId );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), minimumEntityId );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), maximumEntityId );
	}

	@AfterAll
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testRead(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), emptyEntityId );
			factory.createEmptyEntity().assertEqual( entity1, MappedSuperclassOfBasics.EqualityCheck.OTHER );

			T entity2 = em.find( factory.getEntityClass(), minimumEntityId );
			factory.createMinimumEntity().assertEqual( entity2, MappedSuperclassOfBasics.EqualityCheck.OTHER );

			T entity3 = em.find( factory.getEntityClass(), maximumEntityId );
			factory.createMaximumEntity().assertEqual( entity3, MappedSuperclassOfBasics.EqualityCheck.OTHER );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsArbitraryNumerics.class)
	public void testReadNumeric(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), emptyEntityId );
			factory.createEmptyEntity().assertEqual( entity1, MappedSuperclassOfBasics.EqualityCheck.NUMERICS );

			T entity2 = em.find( factory.getEntityClass(), minimumEntityId );
			factory.createMinimumEntity().assertEqual( entity2, MappedSuperclassOfBasics.EqualityCheck.NUMERICS );

			T entity3 = em.find( factory.getEntityClass(), maximumEntityId );
			factory.createMaximumEntity().assertEqual( entity3, MappedSuperclassOfBasics.EqualityCheck.NUMERICS );
		} );
	}

}
