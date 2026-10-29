/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.singletable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;
import org.hibernate.basic.test.mapping.inheritance.InheritanceEntityFactory;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class LongIdSingleTableEntity extends AbstractInheritanceEntity<Long> {

	public static final InheritanceEntityFactory<Long, LongIdSingleTableEntity> FACTORY = new InheritanceEntityFactory<>() {
		@Override
		public Class<LongIdSingleTableEntity> getEntityClass() {
			return LongIdSingleTableEntity.class;
		}

		@Override
		public Class<? extends LongIdSingleTableEntity> getEntity1Class() {
			return LongIdSingleTableEntitySub1.class;
		}

		@Override
		public Class<? extends LongIdSingleTableEntity> getEntity2Class() {
			return LongIdSingleTableEntitySub2.class;
		}

		@Override
		public LongIdSingleTableEntity createEntity1() {
			return new LongIdSingleTableEntitySub1( 1L, "LongIdInheritanceEntity1", 1 );
		}

		@Override
		public LongIdSingleTableEntity createEntity2() {
			return new LongIdSingleTableEntitySub2( 2L, "LongIdInheritanceEntity2", "abc" );
		}
	};

	@Id
	private Long id;

	public LongIdSingleTableEntity() {
	}

	public LongIdSingleTableEntity(Long id, String name) {
		super( name );
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
}
