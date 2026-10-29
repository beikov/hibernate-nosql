/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone.bidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToOne;
import org.hibernate.basic.test.mapping.onetoone.AbstractOneToOneEntity;
import org.hibernate.basic.test.mapping.onetoone.OneToOneEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class LongIdOneToOneJoinTableBidirEntity extends AbstractOneToOneEntity<Long, SimpleLongIdOneToOneJoinTableInverseEntity> {

	public static final OneToOneEntityFactory<Long, LongIdOneToOneJoinTableBidirEntity> FACTORY = new OneToOneEntityFactory<>() {
		@Override
		public Class<LongIdOneToOneJoinTableBidirEntity> getEntityClass() {
			return LongIdOneToOneJoinTableBidirEntity.class;
		}

		@Override
		public Class<?> getElementEntityClass() {
			return SimpleLongIdOneToOneJoinTableInverseEntity.class;
		}

		@Override
		public LongIdOneToOneJoinTableBidirEntity createEmptyEntity() {
			return new LongIdOneToOneJoinTableBidirEntity( 1L, "LongIdOneToOneJoinTableBidirEntity1" );
		}

		@Override
		public LongIdOneToOneJoinTableBidirEntity createSimpleEntity() {
			LongIdOneToOneJoinTableBidirEntity entity = new LongIdOneToOneJoinTableBidirEntity( 2L, "LongIdOneToOneJoinTableBidirEntity2" );
			SimpleLongIdOneToOneJoinTableInverseEntity simpleEntity = SimpleLongIdOneToOneJoinTableInverseEntity.FACTORY.createEntity();
			entity.setTheOneToOne( simpleEntity );
			simpleEntity.setTheOneToOne( entity );
			return entity;
		}
	};

	@Id
	private Long id;
	private String name;
	@JoinTable
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private SimpleLongIdOneToOneJoinTableInverseEntity theOneToOne;

	public LongIdOneToOneJoinTableBidirEntity() {
	}

	public LongIdOneToOneJoinTableBidirEntity(Long id, String name) {
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
	public SimpleLongIdOneToOneJoinTableInverseEntity getTheOneToOne() {
		return theOneToOne;
	}

	@Override
	public void setTheOneToOne(SimpleLongIdOneToOneJoinTableInverseEntity theOneToOne) {
		this.theOneToOne = theOneToOne;
	}

	@Override
	public void assertEqual(AbstractOneToOneEntity<Long, SimpleLongIdOneToOneJoinTableInverseEntity> that) {
		super.assertEqual( that );
		assertEquals( getName(), ((LongIdOneToOneJoinTableBidirEntity) that).getName() );
	}
}
