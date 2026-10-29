/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal.collection;

import org.hibernate.collection.spi.AbstractBagSemantics;
import org.hibernate.collection.spi.CollectionInitializerProducer;
import org.hibernate.collection.spi.PersistentBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.mapping.CollectionPart;
import org.hibernate.metamodel.mapping.PluralAttributeMapping;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.DomainResultCreationState;
import org.hibernate.sql.results.graph.Fetch;
import org.hibernate.sql.results.graph.FetchParent;
import org.hibernate.sql.results.graph.collection.internal.BagInitializerProducer;

import java.util.Collection;

public class MilvusBagSemantics<E> extends AbstractBagSemantics<E> {
	/**
	 * Singleton access
	 */
	public static final MilvusBagSemantics<?> INSTANCE = new MilvusBagSemantics<>();

	private MilvusBagSemantics() {
	}

	@Override
	public CollectionInitializerProducer createInitializerProducer(
			NavigablePath navigablePath,
			PluralAttributeMapping attributeMapping,
			FetchParent fetchParent,
			boolean selected,
			String resultVariable,
			DomainResultCreationState creationState) {
		final Fetch idBagIdFetch;
		if ( attributeMapping.getIdentifierDescriptor() != null ) {
			idBagIdFetch = fetchParent.generateFetchableFetch(
					attributeMapping.getIdentifierDescriptor(),
					navigablePath.append( CollectionPart.Nature.ID.getName() ),
					FetchTiming.IMMEDIATE,
					selected,
					null,
					creationState
			);
		}
		else {
			idBagIdFetch = null;
		}

		final Fetch elementFetch = MilvusFetchHelper.createElementFetch( navigablePath, attributeMapping, fetchParent, selected, creationState );
		return new BagInitializerProducer( attributeMapping, idBagIdFetch, elementFetch );
	}

	@Override
	public CollectionClassification getCollectionClassification() {
		return CollectionClassification.BAG;
	}

	@Override
	public PersistentCollection<E> instantiateWrapper(
			Object key,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentBag<>( session );
	}

	@Override
	public PersistentCollection<E> wrap(
			Collection<E> rawCollection,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentBag<>( session, rawCollection );
	}
}
