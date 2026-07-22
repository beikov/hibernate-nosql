/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc.internal;

import io.milvus.v2.service.vector.response.SearchResp;
import org.hibernate.milvus.jdbc.MilvusHelper;

import java.util.List;

public class MilvusSearchResultSet extends AbstractMilvusResultSet {

	private final SearchResp searchResp;

	public MilvusSearchResultSet(MilvusStatement statement, SearchResp searchResp, String collectionName, List<String> fields) {
		super(statement, collectionName, fields);
		this.searchResp = searchResp;
	}

	@Override
	protected int resultSize() {
		return searchResp.getSearchResults().get( 0 ).size();
	}

	@Override
	protected Object getField(int position, String field) {
		SearchResp.SearchResult queryResult = searchResp.getSearchResults().get( 0 ).get( position );
		return switch (field) {
			// Milvus returns the euclidean squared distance to avoid taking the square root
			case MilvusHelper.EUCLIDEAN_DISTANCE_FIELD -> Math.sqrt( queryResult.getScore().doubleValue() );
			// Cosine is given as similarity in the range [-1..1], but we need the distance in [0..2] here
			case MilvusHelper.COSINE_DISTANCE_FIELD -> 1D - queryResult.getScore().doubleValue();
			case MilvusHelper.DISTANCE_FIELD -> queryResult.getScore().doubleValue();
			default -> queryResult.getEntity().get( field );
		};
	}
}
