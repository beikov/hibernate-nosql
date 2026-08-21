package org.hibernate.nosql.testing;

import org.hibernate.boot.ResourceStreamLocator;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.spi.AdditionalMappingContributions;
import org.hibernate.boot.spi.AdditionalMappingContributor;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;
import org.hibernate.milvus.MilvusDialect;

import java.util.List;

public class IndexAdditionalMappingContributor implements AdditionalMappingContributor {
	@Override
	public void contribute(
			AdditionalMappingContributions contributions,
			InFlightMetadataCollector metadata,
			ResourceStreamLocator resourceStreamLocator,
			MetadataBuildingContext buildingContext) {
		if ( metadata.getDatabase().getDialect() instanceof MilvusDialect ) {
			for ( PersistentClass entityBinding : metadata.getEntityBindings() ) {
				final Table table = entityBinding.getTable();
				for ( String metric : List.of( "ip", "cosine", "l2", "hamming", "jaccard" ) ) {
					final String vectorColumnName = "the_" + metric + "_vector";
					final Column column = table.getColumn( Identifier.toIdentifier( vectorColumnName ) );
					if ( column != null ) {
						final Index index = table.getOrCreateIndex( "vec_" + metric );
						index.addColumn( column );
						index.setOptions( "metric=" + metric );
					}
				}
			}
		}
	}
}
