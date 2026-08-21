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
import org.hibernate.vector.SparseFloatVector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.nosql.testing.VectorTestHelper.euclideanNorm;
import static org.hibernate.nosql.testing.VectorTestHelper.innerProduct;
import static org.hibernate.nosql.testing.VectorTestHelper.normalizeVectorString;
import static org.hibernate.nosql.testing.VectorTestHelper.vectorSparseStringLiteral;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(annotatedClasses = SparseIpFloatVectorTest.VectorEntity.class)
@SessionFactory
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService(
				role = AdditionalMappingContributor.class,
				impl = IndexAdditionalMappingContributor.class
		)
)
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsSparseFloatVectorType.class)
@SkipForDialect(dialectClass = PostgresPlusDialect.class, reason = "Test database does not have the extension enabled")
public class SparseIpFloatVectorTest {

	private static final float[] V1 = new float[]{ 0, 2, 3 };
	private static final float[] V2 = new float[]{ 0, 5, 6 };

	@BeforeEach
	public void prepareData(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			em.persist( new VectorEntity( 1L, new SparseFloatVector( V1 ) ) );
			em.persist( new VectorEntity( 2L, new SparseFloatVector( V2 ) ) );
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
			assertArrayEquals( new float[]{ 0, 2, 3 }, tableRecord.getTheIpVector().toDenseVector() );

			tableRecord = em.find( VectorEntity.class, 2L );
			assertArrayEquals( new float[]{ 0, 5, 6 }, tableRecord.getTheIpVector().toDenseVector()  );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = VectorDialectFeatureChecks.SupportsExpressionsInSelectClause.class)
	public void testCast(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final String literal = vectorSparseStringLiteral( new float[] {1, 1, 1}, em );
			final Tuple vector = em.createSelectionQuery( "select cast(e.theIpVector as string), cast('" + literal + "' as sparse_float_vector(3)) from VectorEntity e where e.id = 1", Tuple.class )
					.getSingleResult();
			assertEquals( vectorSparseStringLiteral( V1, em ), normalizeVectorString( vector.get( 0, String.class ) ) );
			assertEquals( new SparseFloatVector( new float[]{ 1, 1, 1 } ), vector.get( 1, SparseFloatVector.class ) );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsInnerProduct.class)
	public void testInnerProduct(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[]{ 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery( "select e.id, inner_product(e.theIpVector, :vec) from VectorEntity e order by 2 desc", Tuple.class )
					.setParameter( "vec", new SparseFloatVector( vector ) )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 1 ).get( 0 ) );
			assertEquals( innerProduct( V1, vector ), results.get( 1 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 0 ).get( 0 ) );
			assertEquals( innerProduct( V2, vector ), results.get( 0 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsInnerProduct.class)
	public void testNegativeInnerProduct(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final float[] vector = new float[]{ 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery( "select e.id, negative_inner_product(e.theIpVector, :vec) from VectorEntity e order by 2 desc", Tuple.class )
					.setParameter( "vec", new SparseFloatVector( vector ) )
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
			final List<Tuple> results = em.createSelectionQuery( "select e.id, vector_dims(e.theIpVector) from VectorEntity e order by e.id", Tuple.class )
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
	public void testVectorNorm(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final List<Tuple> results = em.createSelectionQuery( "select e.id, vector_norm(e.theIpVector) from VectorEntity e order by e.id", Tuple.class )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( euclideanNorm( V1 ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( euclideanNorm( V2 ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Entity( name = "VectorEntity" )
	public static class VectorEntity {

		@Id
		private Long id;

		@Column( name = "the_ip_vector" )
		@JdbcTypeCode(SqlTypes.SPARSE_VECTOR_FLOAT32)
		@Array(length = 3)
		private SparseFloatVector theIpVector;

		public VectorEntity() {
		}

		public VectorEntity(Long id, SparseFloatVector theIpVector) {
			this.id = id;
			this.theIpVector = theIpVector;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public SparseFloatVector getTheIpVector() {
			return theIpVector;
		}

		public void setTheIpVector(SparseFloatVector theVector) {
			this.theIpVector = theVector;
		}
	}
}
