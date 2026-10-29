/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.tableperclass;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;
import org.hibernate.basic.test.mapping.inheritance.InheritanceEntityFactory;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class LongIdTablePerClassEntity extends AbstractInheritanceEntity<Long> {

	public static final InheritanceEntityFactory<Long, LongIdTablePerClassEntity> FACTORY = new InheritanceEntityFactory<>() {
		@Override
		public Class<LongIdTablePerClassEntity> getEntityClass() {
			return LongIdTablePerClassEntity.class;
		}

		@Override
		public Class<? extends LongIdTablePerClassEntity> getEntity1Class() {
			return LongIdTablePerClassEntitySub1.class;
		}

		@Override
		public Class<? extends LongIdTablePerClassEntity> getEntity2Class() {
			return LongIdTablePerClassEntitySub2.class;
		}

		@Override
		public LongIdTablePerClassEntity createEntity1() {
			return new LongIdTablePerClassEntitySub1( 1L, "LongIdInheritanceEntity1", 1 );
		}

		@Override
		public LongIdTablePerClassEntity createEntity2() {
			return new LongIdTablePerClassEntitySub2( 2L, "LongIdInheritanceEntity2", "abc" );
		}
	};

	@Id
	private Long id;

	public LongIdTablePerClassEntity() {
	}

	public LongIdTablePerClassEntity(Long id, String name) {
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
