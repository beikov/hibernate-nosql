/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

final class WrappedTypeAdapterFactory<T> implements TypeAdapterFactory {

	private final Class<T> baseType;
	private final Map<String, Class<? extends T>> nameToSubtype = new LinkedHashMap<>();
	private final Map<Class<? extends T>, String> subtypeToName = new LinkedHashMap<>();

	private WrappedTypeAdapterFactory(Class<T> baseType) {
		this.baseType = baseType;
	}

	static <T> WrappedTypeAdapterFactory<T> of(Class<T> baseType) {
		return new WrappedTypeAdapterFactory<>( baseType );
	}

	WrappedTypeAdapterFactory<T> registerSubtype(Class<? extends T> subtype) {
		final String name = subtype.getSimpleName();
		nameToSubtype.put( name, subtype );
		subtypeToName.put( subtype, name );
		return this;
	}

	@Override
	public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
		if ( type.getRawType() != baseType ) {
			return null;
		}

		final Map<String, TypeAdapter<?>> nameToDelegate = new LinkedHashMap<>();
		final Map<Class<?>, TypeAdapter<?>> classToDelegate = new LinkedHashMap<>();

		for ( Map.Entry<String, Class<? extends T>> entry : nameToSubtype.entrySet() ) {
			final TypeAdapter<?> delegate = gson.getDelegateAdapter( this, TypeToken.get( entry.getValue() ) );
			nameToDelegate.put( entry.getKey(), delegate );
			classToDelegate.put( entry.getValue(), delegate );
		}

		return new TypeAdapter<>() {
			@Override
			public void write(JsonWriter out, R value) throws IOException {
				if ( value == null ) {
					out.nullValue();
					return;
				}
				final Class<?> runtimeType = value.getClass();
				final String name = subtypeToName.get( runtimeType );
				if ( name == null ) {
					throw new JsonParseException( "Unknown subtype: " + runtimeType.getName() );
				}
				@SuppressWarnings("unchecked")
				final TypeAdapter<R> delegate = (TypeAdapter<R>) classToDelegate.get( runtimeType );
				out.beginObject();
				out.name( name );
				delegate.write( out, value );
				out.endObject();
			}

			@Override
			public R read(JsonReader in) throws IOException {
				if ( in.peek() == JsonToken.NULL ) {
					in.nextNull();
					return null;
				}
				in.beginObject();
				final String name = in.nextName();
				final TypeAdapter<?> delegate = nameToDelegate.get( name );
				if ( delegate == null ) {
					throw new JsonParseException( "Unknown type: " + name );
				}
				@SuppressWarnings("unchecked")
				final R result = (R) delegate.read( in );
				in.endObject();
				return result;
			}
		};
	}
}
