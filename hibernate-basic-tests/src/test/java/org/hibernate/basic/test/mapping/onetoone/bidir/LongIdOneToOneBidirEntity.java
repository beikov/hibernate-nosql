/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone.bidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.basic.test.mapping.onetoone.AbstractOneToOneEntity;
import org.hibernate.basic.test.mapping.onetoone.OneToOneEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdOneToOneBidirEntity extends AbstractOneToOneEntity<Long, SimpleLongIdOneToOneInverseEntity> {

	public static final OneToOneEntityFactory<Long, LongIdOneToOneBidirEntity> FACTORY = new OneToOneEntityFactory<>() {
		@Override
		public Class<LongIdOneToOneBidirEntity> getEntityClass() {
			return LongIdOneToOneBidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdOneToOneInverseEntity.class;
		}

		@Override
		public LongIdOneToOneBidirEntity createEmptyEntity() {
			return new LongIdOneToOneBidirEntity( 1L, "LongIdOneToOneBidirEntity1" );
		}

		@Override
		public LongIdOneToOneBidirEntity createSimpleEntity() {
			LongIdOneToOneBidirEntity entity = new LongIdOneToOneBidirEntity( 2L, "LongIdOneToOneBidirEntity2" );
			SimpleLongIdOneToOneInverseEntity simpleEntity = SimpleLongIdOneToOneInverseEntity.FACTORY.createEntity();
			entity.setTheOneToOne( simpleEntity );
			simpleEntity.setTheOneToOne( entity );
			return entity;
		}
	};

	@Id
	private Long id;
	private String name;
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SimpleLongIdOneToOneInverseEntity theOneToOne;

	public LongIdOneToOneBidirEntity() {
	}

	public LongIdOneToOneBidirEntity(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public SimpleLongIdOneToOneInverseEntity getTheOneToOne() {
		return theOneToOne;
	}

	@Override
	public void setTheOneToOne(SimpleLongIdOneToOneInverseEntity theOneToOne) {
		this.theOneToOne = theOneToOne;
	}

	@Override
	public void assertEqual(AbstractOneToOneEntity<Long, SimpleLongIdOneToOneInverseEntity> that) {
		super.assertEqual( that );
		assertEquals( getName(), ((LongIdOneToOneBidirEntity) that).getName() );
	}
}
