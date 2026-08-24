/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.metamodel.mapping.SqlExpressible;
import org.hibernate.type.Type;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

public class Neo4jVectorDdlType extends DdlTypeImpl {

	public Neo4jVectorDdlType(int sqlTypeCode, String typeNamePattern, Dialect dialect) {
		super( sqlTypeCode, typeNamePattern, dialect );
	}

	@Override
	public String getCastTypeName(Size columnSize, SqlExpressible type, DdlTypeRegistry ddlTypeRegistry) {
		String castTypeName = super.getCastTypeName( columnSize, type, ddlTypeRegistry );
		if ( columnSize.getArrayLength() != null ) {
			castTypeName = StringHelper.replaceOnce( castTypeName, "$a", columnSize.getArrayLength().toString() );
		}
		return castTypeName;
	}

	@Override
	public String getTypeName(Size columnSize, Type type, DdlTypeRegistry ddlTypeRegistry) {
		String typeName = super.getTypeName( columnSize, type, ddlTypeRegistry );
		if ( columnSize.getArrayLength() != null ) {
			typeName = StringHelper.replaceOnce( typeName, "$a", columnSize.getArrayLength().toString() );
		}
		return typeName;
	}
}
