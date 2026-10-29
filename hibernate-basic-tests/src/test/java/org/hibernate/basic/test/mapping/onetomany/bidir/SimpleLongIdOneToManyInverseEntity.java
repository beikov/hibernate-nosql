/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.onetomany.bidir;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.hibernate.basic.test.entity.AbstractSimpleEntity;
import org.hibernate.basic.test.entity.SimpleEntityFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Entity
public class SimpleLongIdOneToManyInverseEntity extends AbstractSimpleEntity<Long> {

	public static final SimpleEntityFactory<Long, SimpleLongIdOneToManyInverseEntity> FACTORY = new SimpleEntityFactory<>() {
		@Override
		public Class<SimpleLongIdOneToManyInverseEntity> getEntityClass() {
			return SimpleLongIdOneToManyInverseEntity.class;
		}

		@Override
		public SimpleLongIdOneToManyInverseEntity createEntity() {
			return new SimpleLongIdOneToManyInverseEntity(1L, "SimpleLongEntity1");
		}
	};


	@Id
	private Long id;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private LongIdOneToManyBidirEntity theManyToOne;

	public SimpleLongIdOneToManyInverseEntity() {
	}

	public SimpleLongIdOneToManyInverseEntity(Long id, String name) {
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

	public LongIdOneToManyBidirEntity getTheManyToOne() {
		return theManyToOne;
	}

	public void setTheManyToOne(LongIdOneToManyBidirEntity theManyToOne) {
		this.theManyToOne = theManyToOne;
	}

	@Override
	public void assertEqual(AbstractSimpleEntity<Long> that) {
		super.assertEqual( that );
		SimpleLongIdOneToManyInverseEntity other = (SimpleLongIdOneToManyInverseEntity) that;
		if ( getTheManyToOne() == null || other.getTheManyToOne() == null ) {
			assertEquals( getTheManyToOne(), other.getTheManyToOne() );
		}
		else {
			assertEquals( getTheManyToOne().getId(), other.getTheManyToOne().getId() );
		}
	}
}
