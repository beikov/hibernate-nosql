/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.spi.Exporter;

/**
 * Milvus does not support foreign keys, so we ignore them.
 */
public class MilvusForeignKeyExporter implements Exporter<ForeignKey> {

	public static final MilvusForeignKeyExporter INSTANCE = new MilvusForeignKeyExporter();

	@Override
	public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata, SqlStringGenerationContext context) {
		return NO_COMMANDS;
	}

	@Override
	public String[] getSqlDropStrings(ForeignKey foreignKey, Metadata metadata, SqlStringGenerationContext context) {
		return NO_COMMANDS;
	}
}
