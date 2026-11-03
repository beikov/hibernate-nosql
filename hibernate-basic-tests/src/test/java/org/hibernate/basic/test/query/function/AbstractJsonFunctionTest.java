/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.function;

import org.hibernate.basic.test.entity.json.AbstractJsonEntity;
import org.hibernate.basic.test.entity.json.JsonEntityFactory;
import org.hibernate.milvus.MilvusDialect;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.SkipForDialect;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public abstract class AbstractJsonFunctionTest<ID, T extends AbstractJsonEntity<ID>> {

	private final JsonEntityFactory<ID, T> factory;

	private Object simpleEntityId;

	public AbstractJsonFunctionTest(JsonEntityFactory<ID, T> factory) {
		this.factory = factory;
	}

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			T simpleEntity = factory.createEntity();
			simpleEntity.setTheJson( """
					{
						"theInteger": 1,
						"theString": "test",
						"theBoolean": true,
						"theNumber": 1.1,
						"theNull": null,
						"theArray": [1, 2, 3],
						"theObject": {
							"id": 1,
							"name": "test"
						},
						"theObjectArray": [
							{
								"id": 1,
								"name": "test"
							},
							{
								"id": 2,
								"name": "test2"
							}
						],
						"the \\"special\\" \\\\property": "test"
					}
					""" );
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
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueInteger(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theInteger", 1 );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueString(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theString", "test" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueBoolean(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theBoolean", true );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueNumber(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theNumber", 1.1d );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueNull(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theNull", null );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueNonExisting(SessionFactoryScope scope) {
		testJsonValue( scope, "$.nonExisting", null );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueObjectElement(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theObject.name", "test" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueArrayElement(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theArray[0]", 1 );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueNonExistingArrayElement(SessionFactoryScope scope) {
		testJsonValue( scope, "$.theArray[3]", null );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonValue.class)
	public void testJsonValueQuotedJson(SessionFactoryScope scope) {
		testJsonValue( scope, "$.the \"special\" \\property", "test" );
	}

	private void testJsonValue(SessionFactoryScope scope, String jsonPath, Object result) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			if ( result == null ) {
				cq.where( cb.isNull( cb.jsonValue( root.get( "theJson" ), jsonPath ) ) );
			}
			else {
				cq.where( cb.equal(
						cb.jsonValue( root.get( "theJson" ), jsonPath, result.getClass() ),
						result
				) );
			}
			final var query = em.createSelectionQuery( cq );
			final var entity = query.getSingleResult();
			assertEquals( simpleEntityId, entity.getId() );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonExistsInteger(SessionFactoryScope scope) {
		testJsonExists( scope, "$.theInteger" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	@SkipForDialect(dialectClass = MilvusDialect.class, reason = "Milvus does not have an operator to understand the difference between null and missing")
	public void testJsonExistsNull(SessionFactoryScope scope) {
		testJsonExists( scope, "$.theNull" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonNotExists(SessionFactoryScope scope) {
		testJsonNotExists( scope, "$.nonExisting" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonExistsQuotedJson(SessionFactoryScope scope) {
		testJsonExists( scope, "$.the \"special\" \\property" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonExistsObject(SessionFactoryScope scope) {
		testJsonExists( scope, "$.theObject" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonExistsObjectElement(SessionFactoryScope scope) {
		testJsonExists( scope, "$.theObject.name" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonNotExistsObjectElement(SessionFactoryScope scope) {
		testJsonNotExists( scope, "$.theObject.nonExisting" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonExistsArrayElement(SessionFactoryScope scope) {
		testJsonExists( scope, "$.theArray[0]" );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonExists.class)
	public void testJsonNotExistsArrayElement(SessionFactoryScope scope) {
		testJsonNotExists( scope, "$.theArray[3]" );
	}

	private void testJsonExists(SessionFactoryScope scope, String jsonPath) {
		testJsonExists( scope, jsonPath, true );
	}

	private void testJsonNotExists(SessionFactoryScope scope, String jsonPath) {
		testJsonExists( scope, jsonPath, false );
	}

	private void testJsonExists(SessionFactoryScope scope, String jsonPath, boolean exists) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			cq.where( cb.jsonExists( root.get( "theJson" ), jsonPath ) );
			final var query = em.createSelectionQuery( cq );
			if ( exists ) {
				final var entity = query.getSingleResult();
				assertEquals( simpleEntityId, entity.getId() );
			}
			else {
				assertNull( query.getSingleResultOrNull() );
			}
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJsonQuery.class)
	public void testJsonQueryBasicArray(SessionFactoryScope scope) {
		testJsonQuery( scope, "$.theArray", "[1,2,3]" );
	}

	private void testJsonQuery(SessionFactoryScope scope, String jsonPath, String result) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( factory.getEntityClass() );
			final var root = cq.from( factory.getEntityClass() );
			if ( result == null ) {
				cq.where( cb.isNull( cb.jsonQuery( root.get( "theJson" ), jsonPath ) ) );
			}
			else {
				cq.where( cb.equal(
						cb.jsonQuery( root.get( "theJson" ), jsonPath ),
						result
				) );
			}
			final var query = em.createSelectionQuery( cq );
			final var entity = query.getSingleResult();
			assertEquals( simpleEntityId, entity.getId() );
		} );
	}

}
