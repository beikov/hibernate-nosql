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
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.OracleDialect;
import org.hibernate.dialect.PostgresPlusDialect;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.nosql.testing.IndexAdditionalMappingContributor;
import org.hibernate.nosql.testing.VectorDialectFeatureChecks;
import org.hibernate.testing.orm.junit.BootstrapServiceRegistry;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.SkipForDialect;
import org.hibernate.type.SqlTypes;
import org.hibernate.vector.internal.VectorHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.nosql.testing.VectorTestHelper.cosineDistance;
import static org.hibernate.nosql.testing.VectorTestHelper.euclideanDistance;
import static org.hibernate.nosql.testing.VectorTestHelper.euclideanNorm;
import static org.hibernate.nosql.testing.VectorTestHelper.euclideanNormalize;
import static org.hibernate.nosql.testing.VectorTestHelper.euclideanSquaredDistance;
import static org.hibernate.nosql.testing.VectorTestHelper.innerProduct;
import static org.hibernate.nosql.testing.VectorTestHelper.taxicabDistance;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(annotatedClasses = FloatVectorTest.VectorEntity.class)
@SessionFactory
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService(
				role = AdditionalMappingContributor.class,
				impl = IndexAdditionalMappingContributor.class
		)
)
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsVectorType.class)
@SkipForDialect(dialectClass = PostgresPlusDialect.class, reason = "Test database does not have the extension enabled")
public class FloatVectorTest {

	protected static final float[] V1 = new float[] { 1, 2, 3 };
	protected static final float[] V2 = new float[] { 4, 5, 6 };

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			em.persist( new VectorEntity( 1L, V1 ) );
			em.persist( new VectorEntity( 2L, V2 ) );
		} );
		EventualConsistentTestHelper.awaitExisting( scope, VectorEntity.class, 1L );
		EventualConsistentTestHelper.awaitExisting( scope, VectorEntity.class, 2L );
	}

	@AfterEach
	public void cleanup(SessionFactoryScope scope) {
		scope.dropData();
	}

	@Test
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
	@RequiresDialectFeature(feature = VectorDialectFeatureChecks.SupportsExpressionsInSelectClause.class)
	public void testCast(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final Tuple vector = em.createSelectionQuery( "select cast(e.theIpVector as string), cast('[1, 1, 1]' as vector(3)) from VectorEntity e where e.id = 1", Tuple.class )
					.getSingleResult();
			assertArrayEquals( new float[]{ 1, 2, 3 }, VectorHelper.parseFloatVector( vector.get( 0, String.class ) ) );
			assertArrayEquals( new float[]{ 1, 1, 1 }, vector.get( 1, float[].class ) );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsCosineDistance.class)
	@SkipForDialect(dialectClass = MySQLDialect.class, reason = "Only MySQL HeatWave supports this function")
	public void testCosineDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, cosine_distance(e.theCosineVector, :vec) from VectorEntity e order by 2",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 2L, results.get( 0 ).get( 0 ) );
			assertEquals( cosineDistance( V2, vector ), results.get( 0 ).get( 1, double.class ), 0.0000001D );
			assertEquals( 1L, results.get( 1 ).get( 0 ) );
			assertEquals( cosineDistance( V1, vector ), results.get( 1 ).get( 1, double.class ), 0.0000001D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsEuclideanDistance.class)
	@SkipForDialect(dialectClass = MySQLDialect.class, reason = "Only MySQL HeatWave supports this function")
	public void testEuclideanDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, euclidean_distance(e.theL2Vector, :vec) from VectorEntity e order by 2",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( euclideanDistance( V1, vector ), results.get( 0 ).get( 1, double.class ), 0.000001D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( euclideanDistance( V2, vector ), results.get( 1 ).get( 1, double.class ), 0.000001D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsEuclideanSquaredDistance.class)
	public void testEuclideanSquaredDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, euclidean_squared_distance(e.theL2Vector, :vec) from VectorEntity e order by 2",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( euclideanSquaredDistance( V1, vector ), results.get( 0 ).get( 1, double.class ), 0.000001D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( euclideanSquaredDistance( V2, vector ), results.get( 1 ).get( 1, double.class ), 0.000001D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsTaxicabDistance.class)
	public void testTaxicabDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, taxicab_distance(e.theL1Vector, :vec) from VectorEntity e order by 2",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( taxicabDistance( V1, vector ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( taxicabDistance( V2, vector ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsInnerProduct.class)
	@SkipForDialect(dialectClass = MySQLDialect.class, reason = "Only MySQL HeatWave supports this function")
	public void testInnerProduct(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, inner_product(e.theIpVector, :vec) from VectorEntity e order by 2 desc",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 2L, results.get( 0 ).get( 0 ) );
			assertEquals( innerProduct( V2, vector ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 1L, results.get( 1 ).get( 0 ) );
			assertEquals( innerProduct( V1, vector ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsInnerProduct.class)
	@SkipForDialect(dialectClass = MySQLDialect.class, reason = "Only MySQL HeatWave supports this function")
	public void testNegativeInnerProduct(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[] { 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, negative_inner_product(e.theIpVector, :vec) from VectorEntity e order by 2 asc",
							Tuple.class
					)
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 2L, results.get( 0 ).get( 0 ) );
			assertEquals( innerProduct( V2, vector ) * -1, results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 1L, results.get( 1 ).get( 0 ) );
			assertEquals( innerProduct( V1, vector ) * -1, results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsVectorDims.class)
	public void testVectorDims(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, vector_dims(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( V1.length, results.get( 0 ).get( 1 ) );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( V2.length, results.get( 1 ).get( 1 ) );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsVectorNorm.class)
	@SkipForDialect(dialectClass = OracleDialect.class, reason = "Oracle 23.9 bug")
	public void testVectorNorm(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, vector_norm(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( euclideanNorm( V1 ), results.get( 0 ).get( 1, double.class ), 0.0000002D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( euclideanNorm( V2 ), results.get( 1 ).get( 1, double.class ), 0.0000002D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsL2Norm.class)
	@SkipForDialect(dialectClass = OracleDialect.class, reason = "Oracle 23.9 bug")
	public void testL2Norm(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, l2_norm(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( euclideanNorm( V1 ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( euclideanNorm( V2 ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsL2Normalize.class)
	public void testL2Normalize(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, l2_normalize(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertArrayEquals( euclideanNormalize( V1 ), results.get( 0 ).get( 1, float[].class ), 0.0000001f );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertArrayEquals( euclideanNormalize( V2 ), results.get( 1 ).get( 1, float[].class ), 0.0000001f );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsSubvector.class)
	public void testSubvector(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, subvector(e.theIpVector, 1, 1) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( 1, results.get( 0 ).get( 1, float[].class ).length );
			assertEquals( V1[0], results.get( 0 ).get( 1, float[].class )[0], 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( 1, results.get( 1 ).get( 1, float[].class ).length );
			assertEquals( V2[0], results.get( 1 ).get( 1, float[].class )[0], 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsBinaryQuantize.class)
	public void testBinaryQuantize(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery(
							"select e.id, binary_quantize(e.theIpVector) from VectorEntity e order by e.id",
							Tuple.class
					)
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertArrayEquals( new byte[]{(byte) 0b11100000}, results.get( 0 ).get( 1, byte[].class ) );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertArrayEquals( new byte[]{(byte) 0b11100000}, results.get( 1 ).get( 1, byte[].class ) );
		} );
	}

	@Entity(name = "VectorEntity")
	public static class VectorEntity {

		@Id
		private Long id;

		@Column( name = "the_ip_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR)
		@Array(length = 3)
		private float[] theIpVector;
		@Column( name = "the_cosine_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR)
		@Array(length = 3)
		private float[] theCosineVector;
		@Column( name = "the_l1_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR)
		@Array(length = 3)
		private float[] theL1Vector;
		@Column( name = "the_l2_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR)
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
