/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.neo4j.Neo4jDialect;

import java.util.Locale;

public class Neo4jDialectResolver implements DialectResolver {

	@Override
	public Dialect resolveDialect(DialectResolutionInfo info) {
		return info.getDatabaseName().toLowerCase( Locale.ROOT ).contains( "neo4j" )
				? new Neo4jDialect( info )
				: null;
	}
}
