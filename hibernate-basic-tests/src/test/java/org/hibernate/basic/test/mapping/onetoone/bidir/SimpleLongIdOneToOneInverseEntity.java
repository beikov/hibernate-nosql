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
public class SimpleLongIdOneToOneInverseEntity extends AbstractSimpleEntity<Long> {

	public static final SimpleEntityFactory<Long, SimpleLongIdOneToOneInverseEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleLongIdOneToOneInverseEntity> getEntityClass() {
			return SimpleLongIdOneToOneInverseEntity.class;
		}

		@Override
		public SimpleLongIdOneToOneInverseEntity createEntity() {
			return new SimpleLongIdOneToOneInverseEntity(1L, "SimpleLongEntity1");
		}
	};


	@Id
	private Long id;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "theOneToOne")
	private LongIdOneToOneBidirEntity theOneToOne;

	public SimpleLongIdOneToOneInverseEntity() {
	}

	public SimpleLongIdOneToOneInverseEntity(Long id, String name) {
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

	public LongIdOneToOneBidirEntity getTheOneToOne() {
		return theOneToOne;
	}

	public void setTheOneToOne(LongIdOneToOneBidirEntity theOneToOne) {
		this.theOneToOne = theOneToOne;
	}

	@Override
	public void assertEqual(AbstractSimpleEntity<Long> that) {
		super.assertEqual( that );
		SimpleLongIdOneToOneInverseEntity other = (SimpleLongIdOneToOneInverseEntity) that;
		if ( getTheOneToOne() == null || other.getTheOneToOne() == null ) {
			assertEquals( getTheOneToOne(), other.getTheOneToOne() );
		}
		else {
			assertEquals( getTheOneToOne().getId(), other.getTheOneToOne().getId() );
		}
	}
}
