/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.vector.test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.boot.spi.AdditionalMappingContributor;
import org.hibernate.nosql.testing.EventualConsistentTestHelper;
import org.hibernate.nosql.testing.IndexAdditionalMappingContributor;
import org.hibernate.testing.orm.junit.BootstrapServiceRegistry;
import org.hibernate.testing.orm.junit.DialectFeatureChecks;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.RequiresDialectFeature;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.type.SqlTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@DomainModel(annotatedClasses = BinaryFloat32VectorTest.VectorEntity.class)
@SessionFactory
@BootstrapServiceRegistry(
		javaServices = @BootstrapServiceRegistry.JavaService(
				role = AdditionalMappingContributor.class,
				impl = IndexAdditionalMappingContributor.class
		)
)
@RequiresDialectFeature(feature = DialectFeatureChecks.SupportsFloatVectorType.class)
public class BinaryFloat32VectorTest extends BinaryFloatVectorTest {

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
			assertArrayEquals( new float[] { 1, 2, 3 }, tableRecord.getTheHammingVector(), 0 );
			assertArrayEquals( new float[] { 1, 2, 3 }, tableRecord.getTheJaccardVector(), 0 );

			tableRecord = em.find( VectorEntity.class, 2L );
			assertArrayEquals( new float[] { 4, 5, 6 }, tableRecord.getTheHammingVector(), 0 );
			assertArrayEquals( new float[] { 4, 5, 6 }, tableRecord.getTheJaccardVector(), 0 );
		} );
	}

	@Entity(name = "VectorEntity")
	public static class VectorEntity {

		@Id
		private Long id;

		@Column( name = "the_hamming_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT32)
		@Array(length = 3)
		private float[] theHammingVector;
		@Column( name = "the_jaccard_vector", nullable = false )
		@JdbcTypeCode(SqlTypes.VECTOR_FLOAT32)
		@Array(length = 3)
		private float[] theJaccardVector;


		public VectorEntity() {
		}

		public VectorEntity(Long id, float[] theVector) {
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

		public float[] getTheHammingVector() {
			return theHammingVector;
		}

		public void setTheHammingVector(float[] theHammingVector) {
			this.theHammingVector = theHammingVector;
		}

		public float[] getTheJaccardVector() {
			return theJaccardVector;
		}

		public void setTheJaccardVector(float[] theJaccardVector) {
			this.theJaccardVector = theJaccardVector;
		}
	}
}
