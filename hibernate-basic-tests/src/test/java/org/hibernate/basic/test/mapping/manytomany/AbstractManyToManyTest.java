/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.manytomany;

import jakarta.persistence.metamodel.PluralAttribute;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractManyToManyTest<ID, T extends AbstractManyToManyEntity<ID, OTHER>, OTHER extends AbstractSimpleEntity<ID>> {

	private final ManyToManyEntityFactory<ID, T> factory;

	private Object emptyEntityId;
	private Object simpleEntityId;
	private List<Object> elementIds;

	public AbstractManyToManyTest(ManyToManyEntityFactory<ID, T> factory) {
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
			elementIds = simpleEntity.getTheManyToMany().stream().map( AbstractSimpleEntity::getId ).collect( Collectors.toUnmodifiableList());
		} );
		final var entityType = scope.getSessionFactory().getJpaMetamodel().entity( factory.getEntityClass() );
		EventualConsistentTestHelper.awaitExisting( scope, factory.getEntityClass(), emptyEntityId );
		for (var id : List.of( simpleEntityId ) ) {
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
		for ( Object elementId : elementIds ) {
			EventualConsistentTestHelper.awaitExisting( scope, factory.getElementEntityClass(), elementId );
		}
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
