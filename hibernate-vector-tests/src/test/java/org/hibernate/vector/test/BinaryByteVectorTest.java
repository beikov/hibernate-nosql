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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hibernate.nosql.testing.VectorTestHelper.hammingDistance;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DomainModel(annotatedClasses = BinaryByteVectorTest.VectorEntity.class)
@SessionFactory
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService(
				role = AdditionalMappingContributor.class,
				impl = IndexAdditionalMappingContributor.class
		)
)
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsByteVectorType.class)
@RequiresDialectFeature(feature = VectorDialectFeatureChecks.SupportsBinaryOperationsOnNonBinaryVector.class)
public class BinaryByteVectorTest {

	private static final byte[] V1 = new byte[]{ 1, 2, 3 };
	private static final byte[] V2 = new byte[]{ 4, 5, 6 };

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
			assertArrayEquals( new byte[]{ 1, 2, 3 }, tableRecord.getTheHammingVector() );
			assertArrayEquals( new byte[]{ 1, 2, 3 }, tableRecord.getTheJaccardVector() );

			tableRecord = em.find( VectorEntity.class, 2L );
			assertArrayEquals( new byte[]{ 4, 5, 6 }, tableRecord.getTheHammingVector() );
			assertArrayEquals( new byte[]{ 4, 5, 6 }, tableRecord.getTheJaccardVector() );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsHammingDistance.class)
	public void testHammingDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final byte[] vector = new byte[]{ 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery( "select e.id, hamming_distance(e.theHammingVector, :vec) from VectorEntity e order by 2", Tuple.class )
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( hammingDistance( V1, vector ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( hammingDistance( V2, vector ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Test
	@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsJaccardDistance.class)
	public void testJaccardDistance(SessionFactoryScope scope) {
		scope.inTransaction( em -> {
			final byte[] vector = new byte[]{ 1, 1, 1 };
			final List<Tuple> results = em.createSelectionQuery( "select e.id, hamming_distance(e.theJaccardVector, :vec) from VectorEntity e order by 2", Tuple.class )
					.setParameter( "vec", vector )
					.getResultList();
			assertEquals( 2, results.size() );
			assertEquals( 1L, results.get( 0 ).get( 0 ) );
			assertEquals( hammingDistance( V1, vector ), results.get( 0 ).get( 1, double.class ), 0D );
			assertEquals( 2L, results.get( 1 ).get( 0 ) );
			assertEquals( hammingDistance( V2, vector ), results.get( 1 ).get( 1, double.class ), 0D );
		} );
	}

	@Entity( name = "VectorEntity" )
	public static class VectorEntity {

		@Id
		private Long id;
		@Column( name = "the_hamming_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_INT8)
		@Array(length = 3)
		private byte[] theHammingVector;
		@Column( name = "the_jaccard_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_INT8)
		@Array(length = 3)
		private byte[] theJaccardVector;

		public VectorEntity() {
		}

		public VectorEntity(Long id, byte[] theVector) {
			this.id = id;
			this.theHammingVector = theVector;
			this.theJaccardVector = theVector;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public byte[] getTheHammingVector() {
			return theHammingVector;
		}

		public void setTheHammingVector(byte[] theHammingVector) {
			this.theHammingVector = theHammingVector;
		}

		public byte[] getTheJaccardVector() {
			return theJaccardVector;
		}

		public void setTheJaccardVector(byte[] theJaccardVector) {
			this.theJaccardVector = theJaccardVector;
		}
	}
}
