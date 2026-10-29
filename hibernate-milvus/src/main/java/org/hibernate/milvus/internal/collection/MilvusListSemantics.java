/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal.collection;

import org.hibernate.collection.spi.CollectionInitializerProducer;
import org.hibernate.collection.spi.CollectionSemantics;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentList;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.mapping.CollectionPart;
import org.hibernate.metamodel.mapping.PluralAttributeMapping;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.DomainResultCreationState;
import org.hibernate.sql.results.graph.Fetch;
import org.hibernate.sql.results.graph.FetchParent;
import org.hibernate.sql.results.graph.collection.internal.ListInitializerProducer;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class MilvusListSemantics<E> implements CollectionSemantics<List<E>, E> {
	/**
	 * Singleton access
	 */
	public static final MilvusListSemantics<?> INSTANCE = new MilvusListSemantics<>();

	private MilvusListSemantics() {
	}

	@Override
	public CollectionInitializerProducer createInitializerProducer(
			NavigablePath navigablePath,
			PluralAttributeMapping attributeMapping,
			FetchParent fetchParent,
			boolean selected,
			String resultVariable,
			DomainResultCreationState creationState) {
		final Fetch indexFetch = fetchParent.generateFetchableFetch(
				attributeMapping.getIndexDescriptor(),
				navigablePath.append( CollectionPart.Nature.INDEX.getName() ),
				FetchTiming.IMMEDIATE,
				selected,
				null,
				creationState
		);

		final Fetch elementFetch = MilvusFetchHelper.createElementFetch( navigablePath, attributeMapping, fetchParent, selected, creationState );
		return new ListInitializerProducer( attributeMapping, indexFetch, elementFetch );
	}

	@Override
	public CollectionClassification getCollectionClassification() {
		return CollectionClassification.LIST;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<List> getCollectionJavaType() {
		return List.class;
	}

	@Override
	public List<E> instantiateRaw(
			int anticipatedSize,
			CollectionPersister collectionDescriptor) {
		return CollectionHelper.arrayList( anticipatedSize );
	}

	@Override
	public Iterator<E> getElementIterator(List<E> rawCollection) {
		return rawCollection.iterator();
	}

	@Override
	public void visitElements(List<E> rawCollection, Consumer<? super E> action) {
		rawCollection.forEach( action );
	}

	@Override
	public PersistentCollection<E> instantiateWrapper(
			Object key,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentList<>( session );
	}

	@Override
	public PersistentCollection<E> wrap(
			List<E> rawCollection,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentList<>( session, rawCollection );
	}
}
