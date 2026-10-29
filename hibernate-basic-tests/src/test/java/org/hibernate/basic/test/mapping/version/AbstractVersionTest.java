/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.version;

import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsOptimisticLocking.class)
public abstract class AbstractVersionTest<ID, T extends AbstractVersionEntity<ID>> {

	private final VersionEntityFactory<ID, T> factory;

	private Object simpleEntityId;

	public AbstractVersionTest(VersionEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T simpleEntity = factory.createSimpleEntity();
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
	public void testReadAndUpdate(SessionFactoryScope scope) {
		Long oldVersion = scope.fromTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), simpleEntityId );
			factory.createSimpleEntity().assertEqual( entity1 );
			Long version = entity1.getVersion();
			assertNotNull( version );
			entity1.setName( "updated" );
			return version;
		} );
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), simpleEntityId );
			T expectedEntity = factory.createSimpleEntity();
			expectedEntity.setName( "updated" );
			expectedEntity.assertEqual( entity1 );
			assertNotNull( entity1.getVersion() );
			assertNotEquals( oldVersion, entity1.getVersion() );
		} );
	}

}
