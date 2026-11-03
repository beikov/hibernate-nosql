/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MilvusJsonHelper {
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(
					WrappedTypeAdapterFactory.of( MilvusStatementDefinition.class )
							.registerSubtype( MilvusCreateCollection.class )
							.registerSubtype( MilvusDropCollection.class )
							.registerSubtype( MilvusTruncateCollection.class )
							.registerSubtype( MilvusQuery.class )
							.registerSubtype( MilvusSearch.class )
							.registerSubtype( MilvusHybridSearch.class )
							.registerSubtype( MilvusInsert.class )
							.registerSubtype( MilvusUpsert.class )
							.registerSubtype( MilvusDelete.class )
			)
			.registerTypeAdapterFactory(
					WrappedTypeAdapterFactory.of( MilvusTypedValue.class )
							.registerSubtype( MilvusStringValue.class )
							.registerSubtype( MilvusNumberValue.class )
							.registerSubtype( MilvusBooleanValue.class )
							.registerSubtype( MilvusFloatVectorValue.class )
							.registerSubtype( MilvusParameterValue.class )
			)
			.registerTypeAdapterFactory(
					WrappedTypeAdapterFactory.of( MilvusRanker.class )
							.registerSubtype( MilvusRRFRanker.class )
							.registerSubtype( MilvusWeightedRanker.class )
			)
			.create();

	public static MilvusStatementDefinition parseDefinition(String json) {
		return GSON.fromJson( json, MilvusStatementDefinition.class );
	}

	public static String serializeDefinition(MilvusStatementDefinition definition) {
		return GSON.toJson( definition, MilvusStatementDefinition.class );
	}
}
