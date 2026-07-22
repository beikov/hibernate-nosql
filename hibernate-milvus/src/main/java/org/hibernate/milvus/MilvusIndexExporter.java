/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.mapping.Index;
import org.hibernate.tool.schema.spi.Exporter;

public class MilvusIndexExporter implements Exporter<Index> {

	public static final MilvusIndexExporter INSTANCE = new MilvusIndexExporter();

	@Override
	public String[] getSqlCreateStrings(Index exportable, Metadata metadata, SqlStringGenerationContext context) {
		// Handled via MilvusTableExporter
		return new String[0];
	}

	@Override
	public String[] getSqlDropStrings(Index exportable, Metadata metadata, SqlStringGenerationContext context) {
		// Handled via MilvusTableExporter
		return new String[0];
	}
}
