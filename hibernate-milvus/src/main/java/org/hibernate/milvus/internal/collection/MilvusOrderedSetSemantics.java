/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal.collection;

import org.hibernate.collection.spi.AbstractSetSemantics;
import org.hibernate.collection.spi.CollectionInitializerProducer;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.mapping.PluralAttributeMapping;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.DomainResultCreationState;
import org.hibernate.sql.results.graph.Fetch;
import org.hibernate.sql.results.graph.FetchParent;
import org.hibernate.sql.results.graph.collection.internal.SetInitializerProducer;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class MilvusOrderedSetSemantics<E> extends AbstractSetSemantics<LinkedHashSet<E>, E> {
	/**
	 * Singleton access
	 */
	public static final MilvusOrderedSetSemantics<?> INSTANCE = new MilvusOrderedSetSemantics<>();

	private MilvusOrderedSetSemantics() {
	}

	@Override
	public CollectionInitializerProducer createInitializerProducer(
			NavigablePath navigablePath,
			PluralAttributeMapping attributeMapping,
			FetchParent fetchParent,
			boolean selected,
			String resultVariable,
			DomainResultCreationState creationState) {
		final Fetch elementFetch = MilvusFetchHelper.createElementFetch( navigablePath, attributeMapping, fetchParent, selected, creationState );
		return new SetInitializerProducer( attributeMapping, elementFetch );
	}

	@Override
	public CollectionClassification getCollectionClassification() {
		return CollectionClassification.ORDERED_SET;
	}

	@Override
	public LinkedHashSet<E> instantiateRaw(
			int anticipatedSize,
			CollectionPersister collectionDescriptor) {
		return anticipatedSize < 1 ? CollectionHelper.linkedSet() : CollectionHelper.linkedSetOfSize( anticipatedSize );
	}

	@Override
	public PersistentCollection<E> instantiateWrapper(
			Object key,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentSet<>( session );
	}

	@Override
	public PersistentCollection<E> wrap(
			LinkedHashSet<E> rawCollection,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentSet<>( session, rawCollection );
	}

	@Override
	public Iterator<E> getElementIterator(LinkedHashSet<E> rawCollection) {
		return rawCollection.iterator();
	}
}
