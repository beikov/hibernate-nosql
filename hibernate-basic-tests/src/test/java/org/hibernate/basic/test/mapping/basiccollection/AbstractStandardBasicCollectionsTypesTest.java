/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.basiccollection;

import jakarta.persistence.metamodel.PluralAttribute;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public abstract class AbstractStandardBasicCollectionsTypesTest<ID, T extends MappedSuperclassOfBasicCollections<ID>> {

	private final MappedSuperclassOfBasicCollectionsFactory<ID, T> factory;

	private Object emptyEntityId;
	private Object minimumEntityId;
	private Object maximumEntityId;

	public AbstractStandardBasicCollectionsTypesTest(MappedSuperclassOfBasicCollectionsFactory<ID, T> factory) {
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
		final var entityType = scope.getSessionFactory().getJpaMetamodel().entity( factory.getEntityClass() );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), emptyEntityId );
		for (var id : List.of( minimumEntityId, maximumEntityId ) ) {
			EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), id );
			for ( PluralAttribute<? super T, ?, ?> pluralAttribute : entityType.getPluralAttributes() ) {
				//noinspection unchecked
				EventualConsistentTestHelper.awaitExisting(
						scope,
						factory.getEntityClass(),
						(PluralAttribute<? super T, ? extends Collection<?>, ?>) pluralAttribute,
						id
				);
			}
		}
	}

	@AfterAll
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
	public void testRead(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), emptyEntityId );
			factory.createEmptyEntity().assertEqual( entity1, MappedSuperclassOfBasicCollections.EqualityCheck.OTHER );

			T entity2 = em.find( factory.getEntityClass(), minimumEntityId );
			factory.createMinimumEntity().assertEqual( entity2, MappedSuperclassOfBasicCollections.EqualityCheck.OTHER );

			T entity3 = em.find( factory.getEntityClass(), maximumEntityId );
			factory.createMaximumEntity().assertEqual( entity3, MappedSuperclassOfBasicCollections.EqualityCheck.OTHER );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsArbitraryNumerics.class)
	public void testReadNumeric(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T entity1 = em.find( factory.getEntityClass(), emptyEntityId );
			factory.createEmptyEntity().assertEqual( entity1, MappedSuperclassOfBasicCollections.EqualityCheck.NUMERICS );

			T entity2 = em.find( factory.getEntityClass(), minimumEntityId );
			factory.createMinimumEntity().assertEqual( entity2, MappedSuperclassOfBasicCollections.EqualityCheck.NUMERICS );

			T entity3 = em.find( factory.getEntityClass(), maximumEntityId );
			factory.createMaximumEntity().assertEqual( entity3, MappedSuperclassOfBasicCollections.EqualityCheck.NUMERICS );
		} );
	}

}
