/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.internal;

import org.hibernate.boot.ResourceStreamLocator;
import org.hibernate.boot.spi.AdditionalMappingContributions;
import org.hibernate.boot.spi.AdditionalMappingContributor;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.List;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.Value;
import org.hibernate.milvus.internal.mapping.MilvusArray;
import org.hibernate.milvus.internal.mapping.MilvusBag;
import org.hibernate.milvus.internal.mapping.MilvusIdentifierBag;
import org.hibernate.milvus.internal.mapping.MilvusList;
import org.hibernate.milvus.internal.mapping.MilvusMap;
import org.hibernate.milvus.internal.mapping.MilvusSet;

import java.util.HashMap;

/**
 * Install custom collection semantics to allow reading collections without joins.
 */
public class MilvusMetadataContributor implements AdditionalMappingContributor {
	@Override
	public void contribute(
			AdditionalMappingContributions contributions,
			InFlightMetadataCollector metadata,
			ResourceStreamLocator resourceStreamLocator,
			MetadataBuildingContext buildingContext) {
		final java.util.Collection<Collection> collectionBindings = metadata.getCollectionBindings();
		final java.util.Map<String, Collection> collectionMap = new HashMap<>( collectionBindings.size() );
		for ( Collection collectionBinding : collectionBindings ) {
			if ( collectionBinding instanceof Set set ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusSet( set ) );
			}
			else if ( collectionBinding instanceof Bag bag ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusBag( bag ) );
			}
			else if ( collectionBinding instanceof PrimitiveArray ) {
				collectionMap.put( collectionBinding.getRole(), collectionBinding );
			}
			else if ( collectionBinding instanceof Array array ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusArray( array ) );
			}
			else if ( collectionBinding instanceof List list ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusList( list ) );
			}
			else if ( collectionBinding instanceof Map map ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusMap( map ) );
			}
			else if ( collectionBinding instanceof IdentifierBag identifierBag ) {
				collectionMap.put( collectionBinding.getRole(), new MilvusIdentifierBag( identifierBag ) );
			}
			else {
				collectionMap.put( collectionBinding.getRole(), collectionBinding );
			}
		}
		collectionBindings.clear();
		for ( Collection collection : collectionMap.values() ) {
			metadata.addCollectionBinding( collection );
		}
		for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
			replaceCollections( entityBinding.getProperties(), collectionMap );
		}
	}

	private void replaceCollections(java.util.List<Property> properties, java.util.Map<String, Collection> collectionMap) {
		for ( Property property : properties ) {
			replaceCollections( property, collectionMap );
		}
	}

	private void replaceCollections(Property property, java.util.Map<String, Collection> collectionMap) {
		final Value existingValue = property.getValue();
		if ( existingValue instanceof Collection collection ) {
			property.setValue( collectionMap.get( collection.getRole() ) );
		}
		else if ( existingValue instanceof Component component ) {
			replaceCollections( component.getProperties(), collectionMap );
		}
	}

}
