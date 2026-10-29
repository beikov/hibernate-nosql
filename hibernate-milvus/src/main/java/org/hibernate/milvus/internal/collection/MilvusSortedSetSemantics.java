/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal.collection;

import org.hibernate.collection.spi.AbstractSetSemantics;
import org.hibernate.collection.spi.CollectionInitializerProducer;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentSortedSet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.metamodel.mapping.PluralAttributeMapping;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.spi.NavigablePath;
import org.hibernate.sql.results.graph.DomainResultCreationState;
import org.hibernate.sql.results.graph.Fetch;
import org.hibernate.sql.results.graph.FetchParent;
import org.hibernate.sql.results.graph.collection.internal.SetInitializerProducer;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class MilvusSortedSetSemantics<E> extends AbstractSetSemantics<SortedSet<E>, E> {
	/**
	 * Singleton access
	 */
	public static final MilvusSortedSetSemantics<?> INSTANCE = new MilvusSortedSetSemantics<>();

	private MilvusSortedSetSemantics() {
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
		return CollectionClassification.SORTED_SET;
	}

	@Override
	public Class<SortedSet> getCollectionJavaType() {
		return SortedSet.class;
	}

	@Override
	public SortedSet<E> instantiateRaw(
			int anticipatedSize,
			CollectionPersister collectionDescriptor) {
		return new TreeSet<E>(
				collectionDescriptor == null ? null : (Comparator) collectionDescriptor.getSortingComparator()
		);
	}

	@Override
	public PersistentCollection<E> instantiateWrapper(
			Object key,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		//noinspection unchecked
		return new PersistentSortedSet<>( session, (Comparator<E>) collectionDescriptor.getSortingComparator() );
	}

	@Override
	public PersistentCollection<E> wrap(
			SortedSet<E> rawCollection,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentSortedSet<>( session, rawCollection );
	}

	@Override
	public Iterator<E> getElementIterator(SortedSet<E> rawCollection) {
		return rawCollection.iterator();
	}
}
