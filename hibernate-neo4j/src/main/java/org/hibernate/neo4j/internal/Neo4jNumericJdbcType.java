/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.type.SqlTypes;

import java.sql.Types;

/**
 * Descriptor for {@link SqlTypes#NUMERIC NUMERIC} handling.
 *
 * @author Christian Beikov
 */
public class Neo4jNumericJdbcType extends Neo4jDecimalJdbcType {

	public static final Neo4jNumericJdbcType INSTANCE = new Neo4jNumericJdbcType();

	public Neo4jNumericJdbcType() {
	}

	@Override
	public String toString() {
		return "NumericTypeDescriptor";
	}

	@Override
	public int getJdbcTypeCode() {
		return Types.NUMERIC;
	}
}
