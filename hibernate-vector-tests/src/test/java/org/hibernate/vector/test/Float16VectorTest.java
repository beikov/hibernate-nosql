/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.vector.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Tuple;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.boot.spi.AdditionalMappingContributor;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.nosql.testing.IndexAdditionalMappingContributor;
import org.hibernate.nosql.testing.VectorDialectFeatureChecks;
import org.hibernate.testing.orm.junit.BootstrapServiceRegistry;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.type.SqlTypes;
import org.hibernate.vector.internal.VectorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.nosql.testing.VectorTestHelper.euclideanNormalize;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(annotatedClasses = Float16VectorTest.VectorEntity.class)
@SessionFactory
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService(
				role = AdditionalMappingContributor.class,
				impl = IndexAdditionalMappingContributor.class
		)
)
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsFloat16VectorType.class)
public class Float16VectorTest extends FloatVectorTest {

	@BeforeEach
	@Override
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			em.persist( new VectorEntity( 1L, V1 ) );
			em.persist( new VectorEntity( 2L, V2 ) );
		} );
		EventualConsistentTestHelper.awaitExisting( scope, VectorEntity.class, 1L );
		EventualConsistentTestHelper.awaitExisting( scope, VectorEntity.class, 2L );
	}

	@Test
	@Override
	public void testRead(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			VectorEntity tableRecord;
			tableRecord = em.find( VectorEntity.class, 1L );
			assertArrayEquals( new float[]{ 1, 2, 3 }, tableRecord.getTheIpVector() );
			assertArrayEquals( new float[]{ 1, 2, 3 }, tableRecord.getTheCosineVector() );
			assertArrayEquals( new float[]{ 1, 2, 3 }, tableRecord.getTheL1Vector() );
			assertArrayEquals( new float[]{ 1, 2, 3 }, tableRecord.getTheL2Vector() );

			tableRecord = em.find( VectorEntity.class, 2L );
			assertArrayEquals( new float[]{ 4, 5, 6 }, tableRecord.getTheIpVector() );
			assertArrayEquals( new float[]{ 4, 5, 6 }, tableRecord.getTheCosineVector() );
			assertArrayEquals( new float[]{ 4, 5, 6 }, tableRecord.getTheL1Vector() );
			assertArrayEquals( new float[]{ 4, 5, 6 }, tableRecord.getTheL2Vector() );
		} );
	}

	@Test
	@Override
	@RequiresDialectFeature(feature = VectorDialectFeatureChecks.SupportsExpressionsInSelectClause.class)
	public void testCast(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final Tuple vector = em.createSelectionQuery( "select cast(e.theIpVector as string), cast('[1, 1, 1]' as float16_vector(3)) from VectorEntity e where e.id = 1", Tuple.class )
					.getSingleResult();
			assertArrayEquals( new float[]{ 1, 2, 3 }, VectorHelper.parseFloatVector( vector.get( 0, String.class ) ) );
			assertArrayEquals( new float[]{ 1, 1, 1 }, vector.get( 1, float[].class ) );
		} );
	}

	// Due to lower precision (float16/half-precision floating-point) type usage,
	// we have to give a higher allowed delta since we can't easily calculate with the same precision in Java yet
	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsL2Normalize.class)
	@Override
	public void testL2Normalize(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, l2_normalize(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertArrayEquals( euclideanNormalize( V1 ), results.get( 0 ).get( 1, float[].class ), 0.0002f );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertArrayEquals( euclideanNormalize( V2 ), results.get( 1 ).get( 1, float[].class ), 0.0002f );
		} );
	}

	@Entity(name = "VectorEntity")
	public static class VectorEntity {

		@Id
		private Long id;

		@Column( name = "the_ip_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT16)
		@Array(length = 3)
		private float[] theIpVector;
		@Column( name = "the_cosine_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT16)
		@Array(length = 3)
		private float[] theCosineVector;
		@Column( name = "the_l1_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT16)
		@Array(length = 3)
		private float[] theL1Vector;
		@Column( name = "the_l2_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT16)
		@Array(length = 3)
		private float[] theL2Vector;


		public VectorEntity() {
		}

		public VectorEntity(Long id, float[] theVector) {
			this.id = id;
			this.theIpVector = theVector;
			this.theCosineVector = theVector;
			this.theL1Vector = theVector;
			this.theL2Vector = theVector;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public float[] getTheIpVector() {
			return theIpVector;
		}

		public void setTheIpVector(float[] theIpVector) {
			this.theIpVector = theIpVector;
		}

		public float[] getTheCosineVector() {
			return theCosineVector;
		}

		public void setTheCosineVector(float[] theCosineVector) {
			this.theCosineVector = theCosineVector;
		}

		public float[] getTheL1Vector() {
			return theL1Vector;
		}

		public void setTheL1Vector(float[] theL1Vector) {
			this.theL1Vector = theL1Vector;
		}

		public float[] getTheL2Vector() {
			return theL2Vector;
		}

		public void setTheL2Vector(float[] theL2Vector) {
			this.theL2Vector = theL2Vector;
		}
	}
}
