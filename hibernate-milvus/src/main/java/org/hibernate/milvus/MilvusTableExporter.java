/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Table;
import org.hibernate.milvus.jdbc.MilvusCreateCollection;
import org.hibernate.milvus.jdbc.MilvusDropCollection;
import org.hibernate.milvus.jdbc.MilvusHelper;
import org.hibernate.milvus.jdbc.MilvusJsonHelper;
import org.hibernate.tool.schema.spi.Exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MilvusTableExporter implements Exporter<Table> {

	public static final MilvusTableExporter INSTANCE = new MilvusTableExporter();

	@Override
	public String[] getSqlCreateStrings(Table exportable, Metadata metadata, SqlStringGenerationContext context) {
		final String collectionName = exportable.getQualifiedTableName().getTableName().toString();
		final Collection<Column> columns = exportable.getColumns();

		final Map<String, Integer> fieldsNeedingIndex = new HashMap<>();
		final List<MilvusCreateCollection.IndexParam> fieldIndexes = new ArrayList<>();
		final List<MilvusCreateCollection.FieldSchema> fields = new ArrayList<>( columns.size() );
		final List<MilvusCreateCollection.IndexParam> indexParams = new ArrayList<>( exportable.getIndexes().size() + 1 );
		boolean hasVector = false;
		for ( Column column : columns ) {
			final String fieldName = column.getText( context.getDialect() );
			final boolean primaryKey = exportable.isPrimaryKey( column );
			final String sqlType = column.getSqlType( metadata );
			final DataType dataType;
			final DataType elementType;
			final Integer dimension;
			if ( sqlType.endsWith( " array" ) ) {
				dataType = DataType.Array;
				elementType = MilvusHelper.determineType( sqlType );
				dimension = column.getArrayLength();
			}
			else {
				elementType = null;
				final DataType baseDataType = MilvusHelper.determineType( sqlType );
				if ( primaryKey ) {
					dataType = switch ( baseDataType ) {
						// Milvus requires Int64 for the primary key
						case Int8, Int16, Int32 -> DataType.Int64;
						default -> baseDataType;
					};
					dimension = column.getArrayLength();
					fieldsNeedingIndex.put( fieldName, fields.size() );
				}
				else {
					dataType = baseDataType;
					final boolean isVector = switch ( dataType ) {
						case FloatVector, Float16Vector, BFloat16Vector, BinaryVector, SparseFloatVector -> true;
						default -> false;
					};
					hasVector = hasVector ||  isVector;
					dimension = switch ( dataType ) {
						case FloatVector, Float16Vector, BFloat16Vector, SparseFloatVector -> column.getArrayLength();
						case BinaryVector -> column.getArrayLength() * 8;
						default -> null;
					};
					if ( isVector ) {
						fieldsNeedingIndex.put( fieldName, fields.size() );
					}
				}
			}
			final MilvusCreateCollection.FieldSchema field = new MilvusCreateCollection.FieldSchema(
					fieldName,
					null,
					dataType,
					column.getLength() == null ? null : column.getLength().intValue(),
					dimension,
					primaryKey,
					false,
					false,
					column.isIdentity(),
					elementType,
					null,
					column.isNullable(),
					column.getDefaultValue(),
					false,
					null,
					null
			);
			fields.add( field );
		}

		if ( !hasVector && context.getDialect().getVersion().isSameOrAfter( 2, 4, 2 ) ) {
			fieldsNeedingIndex.put( MilvusSqlAstTranslator.DEFAULT_EMBEDDING_FIELD, fields.size() );
			// As of 2.4.2 a schema requires at least one vector field: https://github.com/milvus-io/milvus/issues/33853
			fields.add( new MilvusCreateCollection.FieldSchema(
					MilvusSqlAstTranslator.DEFAULT_EMBEDDING_FIELD,
					null,
					DataType.FloatVector,
					null,
					2,
					false,
					false,
					false,
					false,
					null,
					null,
					false,
					null,
					false,
					null,
					null
			) );
		}

		for ( Index index : exportable.getIndexes().values() ) {
			String indexName = index.getName();
			String fieldName;

			if ( index.getColumnSpan() != 1 ) {
				throw new IllegalArgumentException("Milvus can only index a single column");
			}
			fieldName = index.getSelectables().get( 0 ).getText( context.getDialect() );

			io.milvus.v2.common.IndexParam.IndexType indexType = IndexParam.IndexType.AUTOINDEX;
			io.milvus.v2.common.IndexParam.MetricType metricType = null;
			Map<String, Object> extraParams = null;

			final String options = index.getOptions();
			if ( options != null ) {
				final String[] optionArray = StringHelper.split( ",", options );
				for ( String option : optionArray ) {
					int assignmentIndex =  option.indexOf('=');
					if ( assignmentIndex != -1 ) {
						final String optionName = option.substring(0, assignmentIndex);
						final String optionValue = option.substring(assignmentIndex + 1);
						switch ( optionName.toLowerCase( Locale.ROOT) ) {
							case "metric" -> metricType = IndexParam.MetricType.valueOf(
									optionValue.toUpperCase( Locale.ROOT ) );
							case "type" ->
									indexType = IndexParam.IndexType.valueOf( optionValue.toUpperCase( Locale.ROOT ) );
							default -> {
								if (extraParams == null) {
									extraParams = new HashMap<>();
								}
								extraParams.put(optionName, optionValue);
							}
						}
					}
					else {
						if (extraParams == null) {
							extraParams = new HashMap<>();
						}
						extraParams.put(option, "true");
					}
				}
			}
			final MilvusCreateCollection.IndexParam indexParam = new MilvusCreateCollection.IndexParam(
					fieldName,
					indexName,
					indexType,
					metricType,
					extraParams
			);
			indexParams.add( indexParam );

			// Register index to avoid auto-creation of index for fields that require indexes
			final Integer fieldIndex = fieldsNeedingIndex.get( fieldName );
			if ( fieldIndex != null ) {
				for ( int i = fieldIndexes.size(); i <= fieldIndex; i++ ) {
					fieldIndexes.add( null );
				}
				final MilvusCreateCollection.IndexParam oldIndex = fieldIndexes.set( fieldIndex, indexParam );
				if ( oldIndex != null ) {
					throw new IllegalArgumentException(
							"Milvus can only have a single index per field, but found two indexes: [" + oldIndex.indexName() + ", " + indexName + "] for field [" + fieldName + "]" );
				}
			}
		}

		// Create automatic indexes that are necessary to load the collection
		for ( var entry : fieldsNeedingIndex.entrySet() ) {
			final var fieldIndex = entry.getValue();
			for ( int i = fieldIndexes.size(); i <= fieldIndex; i++ ) {
				fieldIndexes.add( null );
			}
			final var existingIndex = fieldIndexes.get( fieldIndex );
			if ( existingIndex == null ) {
				final var fieldSchema = fields.get( fieldIndex );
				final var indexName = collectionName + (fieldSchema.isPrimaryKey() ? "_pk" : "_" + fieldSchema.name());
				final var metricType = switch ( fieldSchema.dataType() ) {
					case FloatVector, Float16Vector, BFloat16Vector, SparseFloatVector -> IndexParam.MetricType.COSINE;
					case BinaryVector -> IndexParam.MetricType.HAMMING;
					default -> null;
				};
				final var indexParam = new MilvusCreateCollection.IndexParam(
						fieldSchema.name(),
						indexName,
						IndexParam.IndexType.AUTOINDEX,
						metricType,
						null
				);

				fieldIndexes.set( fieldIndex, indexParam );
				indexParams.add( indexParam );
			}
		}

		final var schema = new MilvusCreateCollection.Schema( fields );
		final var collection = new MilvusCreateCollection(
				collectionName,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				schema,
				indexParams,
				null,
				null,
				null
		);
		return new String[] {MilvusJsonHelper.serializeDefinition( collection )};
	}

	@Override
	public String[] getSqlDropStrings(Table exportable, Metadata metadata, SqlStringGenerationContext context) {
		String collectionName = exportable.getQualifiedTableName().getTableName().toString();
		MilvusDropCollection collection = new MilvusDropCollection(
				collectionName,
				null,
				null
		);
		return new String[] {MilvusJsonHelper.serializeDefinition( collection )};
	}
}
