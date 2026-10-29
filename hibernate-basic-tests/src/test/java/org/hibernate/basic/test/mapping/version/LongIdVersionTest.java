/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.version;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;

@DomainModel(annotatedClasses = { LongIdVersionEntity.class })
@SessionFactory
public class LongIdVersionTest extends AbstractVersionTest<Long, LongIdVersionEntity> {
	public LongIdVersionTest() {
		super( LongIdVersionEntity.FACTORY );
	}
}
