/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j;

import org.hibernate.boot.registry.selector.spi.DialectSelector;
import org.hibernate.dialect.Dialect;

public class Neo4jDialectSelector implements DialectSelector {

	@Override
	public Class<? extends Dialect> resolve(String name) {
		return "Neo4j".equals(name) ? Neo4jDialect.class : null;
	}
}
