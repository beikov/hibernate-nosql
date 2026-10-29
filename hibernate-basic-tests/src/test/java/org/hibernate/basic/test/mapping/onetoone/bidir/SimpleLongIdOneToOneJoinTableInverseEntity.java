/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetoone.bidir;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.basic.test.entity.SimpleEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class SimpleLongIdOneToOneJoinTableInverseEntity extends AbstractSimpleEntity<Long> {

	public static final SimpleEntityFactory<Long, SimpleLongIdOneToOneJoinTableInverseEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleLongIdOneToOneJoinTableInverseEntity> getEntityClass() {
			return SimpleLongIdOneToOneJoinTableInverseEntity.class;
		}

		@Override
		public SimpleLongIdOneToOneJoinTableInverseEntity createEntity() {
			return new SimpleLongIdOneToOneJoinTableInverseEntity(1L, "SimpleLongEntity1");
		}
	};


	@Id
	private Long id;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "theOneToOne")
	private LongIdOneToOneJoinTableBidirEntity theOneToOne;

	public SimpleLongIdOneToOneJoinTableInverseEntity() {
	}

	public SimpleLongIdOneToOneJoinTableInverseEntity(Long id, String name) {
		super(name);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public LongIdOneToOneJoinTableBidirEntity getTheOneToOne() {
		return theOneToOne;
	}

	public void setTheOneToOne(LongIdOneToOneJoinTableBidirEntity theOneToOne) {
		this.theOneToOne = theOneToOne;
	}

	@Override
	public void assertEqual(AbstractSimpleEntity<Long> that) {
		super.assertEqual( that );
		SimpleLongIdOneToOneJoinTableInverseEntity other = (SimpleLongIdOneToOneJoinTableInverseEntity) that;
		if ( getTheOneToOne() == null || other.getTheOneToOne() == null ) {
			assertEquals( getTheOneToOne(), other.getTheOneToOne() );
		}
		else {
			assertEquals( getTheOneToOne().getId(), other.getTheOneToOne().getId() );
		}
	}
}
