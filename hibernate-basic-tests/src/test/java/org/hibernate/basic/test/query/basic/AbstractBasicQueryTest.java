/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.query.basic;

import jakarta.persistence.Tuple;
import org.hibernate.basic.test.mapping.basic.MappedSuperclassOfBasics;
import org.hibernate.basic.test.mapping.basic.MappedSuperclassOfBasicsFactory;
import org.hibernate.nosql.testing.BasicDialectFeatureChecks;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractBasicQueryTest<ID, T extends MappedSuperclassOfBasics<ID>> {

	private final MappedSuperclassOfBasicsFactory<ID, T> factory;

	private Object simpleEntityId;

	public AbstractBasicQueryTest(MappedSuperclassOfBasicsFactory<ID, T> factory) {
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
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsSelectingNonColumns.class)
	public void testArithmetic(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( Tuple.class );
			final var root = cq.from( factory.getEntityClass() );
			cq.where( cb.equal( root.get( "id" ), simpleEntityId ) );
			cq.select( cb.tuple( cb.sum( root.get( "theInteger" ), 1 ) ) );
			final var query = em.createSelectionQuery( cq );
			final var tuple = query.getSingleResult();
			assertEquals( factory.createSimpleEntity().getTheInteger() + 1, tuple.get( 0 ) );
		} );
	}

	@ParameterizedTest
	@MethodSource
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsSelectingNonColumns.class)
	public void testSelectConstants(Object constant, SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final var cb = em.getCriteriaBuilder();
			final var cq = cb.createQuery( Tuple.class );
			final var root = cq.from( factory.getEntityClass() );
			cq.where( cb.equal( root.get( "id" ), simpleEntityId ) );
			cq.select( cb.tuple( cb.literal( constant ) ) );
			final var query = em.createSelectionQuery( cq );
			final var resultList = query.getResultList();
			assertEquals( 1, resultList.size() );
			if ( constant instanceof byte[] bytes ) {
				assertArrayEquals( bytes, (byte[]) resultList.get( 0 ).get( 0 ) );
			}
			else {
				assertEquals( constant, resultList.get( 0 ).get( 0 ) );
			}
		} );
	}

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsUUIDConstant.class)
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsSelectingNonColumns.class)
	public void testSelectUUIDConstant(SessionFactoryScope scope) {
		testSelectConstants( UUID.fromString( "00000000-0000-4000-8000-000000000000"), scope );
	}

	@Test
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsBinaryConstant.class)
	@RequiresDialectFeature(feature = BasicDialectFeatureChecks.SupportsSelectingNonColumns.class)
	public void testSelectBinaryConstant(SessionFactoryScope scope) {
		testSelectConstants( new byte[]{ 1, 2, 3, 4 }, scope );
	}

	public static List<Object> testSelectConstants() {
		return List.of(
				true,
				(byte) 1,
				(short) 1,
				1,
				1L,
				1F,
				1D,
				"test",
				'A',
				BigDecimal.ONE,
				BigInteger.ONE,

				new java.sql.Date( 0L ),
				new java.sql.Time( 0L ),
				new java.sql.Timestamp( 0L ),
				new Date( 0L ),
				LocalDate.EPOCH,
				LocalTime.MIN,
				LocalDate.EPOCH.atStartOfDay(),
				Instant.EPOCH,
				Instant.EPOCH.atOffset( ZoneOffset.UTC ),
				Instant.EPOCH.atZone( ZoneOffset.UTC )
		);
	}

}
