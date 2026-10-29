/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal.collection;

import org.hibernate.collection.spi.AbstractSetSemantics;
import org.hibernate.collection.spi.CollectionInitializerProducer;
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

import java.util.HashSet;
import java.util.Set;

public class MilvusSetSemantics<E> extends AbstractSetSemantics<Set<E>, E> {
	/**
	 * Singleton access
	 */
	public static final MilvusSetSemantics<?> INSTANCE = new MilvusSetSemantics<>();

	private MilvusSetSemantics() {
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
		return CollectionClassification.SET;
	}

	@Override
	public Set<E> instantiateRaw(
			int anticipatedSize,
			CollectionPersister collectionDescriptor) {
		return anticipatedSize < 1 ? new HashSet<>() : CollectionHelper.setOfSize( anticipatedSize );
	}

	@Override
	public PersistentSet<E> instantiateWrapper(
			Object key,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentSet<>( session );
	}

	@Override
	public PersistentSet<E> wrap(
			Set<E> rawCollection,
			CollectionPersister collectionDescriptor,
			SharedSessionContractImplementor session) {
		return new PersistentSet<>( session, rawCollection );
	}
}
