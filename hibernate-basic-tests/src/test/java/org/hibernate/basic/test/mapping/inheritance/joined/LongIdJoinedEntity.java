/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.inheritance.joined;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import org.hibernate.basic.test.mapping.inheritance.AbstractInheritanceEntity;
import org.hibernate.basic.test.mapping.inheritance.InheritanceEntityFactory;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class LongIdJoinedEntity extends AbstractInheritanceEntity<Long> {

	public static final InheritanceEntityFactory<Long, LongIdJoinedEntity> FACTORY = new InheritanceEntityFactory<>() {
		@Override
		public Class<LongIdJoinedEntity> getEntityClass() {
			return LongIdJoinedEntity.class;
		}

		@Override
		public Class<? extends LongIdJoinedEntity> getEntity1Class() {
			return LongIdJoinedEntitySub1.class;
		}

		@Override
		public Class<? extends LongIdJoinedEntity> getEntity2Class() {
			return LongIdJoinedEntitySub2.class;
		}

		@Override
		public LongIdJoinedEntity createEntity1() {
			return new LongIdJoinedEntitySub1( 1L, "LongIdInheritanceEntity1", 1 );
		}

		@Override
		public LongIdJoinedEntity createEntity2() {
			return new LongIdJoinedEntitySub2( 2L, "LongIdInheritanceEntity2", "abc" );
		}
	};

	@Id
	private Long id;

	public LongIdJoinedEntity() {
	}

	public LongIdJoinedEntity(Long id, String name) {
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
