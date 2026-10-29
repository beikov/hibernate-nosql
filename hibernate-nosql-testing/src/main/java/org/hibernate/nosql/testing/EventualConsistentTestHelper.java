/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.nosql.testing;

import jakarta.persistence.metamodel.PluralAttribute;
import org.hibernate.Hibernate;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class EventualConsistentTestHelper {

	public static <X> X awaitExisting(SessionFactoryScope scope, Class<X> clazz, Object id) {
		return findUntil( scope, clazz, id, Optional::isPresent );
	}

	public static <X, C extends Collection<?>> X awaitExisting(SessionFactoryScope scope, Class<X> clazz, PluralAttribute<? super X, C, ?> collectionAttribute, Object id) {
		final Function<X, C> collectionAccessor = getAccessor( collectionAttribute );
		return findUntil(
				scope,
				session -> {
					X entity = session.find( clazz, id );
					if ( entity != null ) {
						C collection = collectionAccessor.apply( entity );
						Hibernate.initialize( collection );
					}
					return entity;
				},
				x -> x.isPresent() && !collectionAccessor.apply( x.get() ).isEmpty(),
				"Could meet acceptance criteria for collection [" + collectionAttribute.getName() + "] of entity [" + clazz.getName() + "] with id " + id + " after 3 retries"
		);
	}

	@SuppressWarnings("unchecked")
	private static <X, C> Function<X, C> getAccessor(PluralAttribute<? super X, C, ?> collectionAttribute) {
		final Member javaMember = collectionAttribute.getJavaMember();
		if ( javaMember instanceof Field field ) {
			field.setAccessible( true );
			return x -> {
				try {
					return (C) field.get( x );
				}
				catch (IllegalAccessException e) {
					throw new RuntimeException( e );
				}
			};
		}
		else if ( javaMember instanceof Method method ) {
			method.setAccessible( true );
			return x -> {
				try {
					return (C) method.invoke( x );
				}
				catch (IllegalAccessException | InvocationTargetException e) {
					throw new RuntimeException( e );
				}
			};
		}
		else {
			throw new UnsupportedOperationException( "Unsupported member type: " + javaMember.getClass().getName() );
		}
	}

	public static <X> X awaitNonExisting(SessionFactoryScope scope, Class<X> clazz, Object id) {
		return findUntil( scope, clazz, id, Optional::isEmpty );
	}

	public static <X> X findExisting(SessionFactoryScope scope, Class<X> clazz, Object id) {
		return findUntil( scope, clazz, id, Optional::isPresent );
	}

	public static <X> X findNonExisting(SessionFactoryScope scope, Class<X> clazz, Object id) {
		return findUntil( scope, clazz, id, Optional::isEmpty );
	}

	public static <X> X findUntil(SessionFactoryScope scope, Class<X> clazz, Object id, Predicate<Optional<X>> acceptancePredicate) {
		return findUntil(
				scope,
				session -> session.find( clazz, id ),
				acceptancePredicate,
				"Could meet acceptance criteria for entity [" + clazz.getName() + "] with id " + id + " after 3 retries"
		);
	}

	private static <X> X findUntil(SessionFactoryScope scope, Function<SessionImplementor, X> loader, Predicate<Optional<X>> acceptancePredicate, String error) {
		int retries = 3;
		while ( 0 < retries-- ) {
			final Optional<X> optional = Optional.ofNullable( scope.fromSession( loader ) );
			if ( acceptancePredicate.test( optional ) ) {
				return optional.orElse( null );
			}
			// Wait a bit, since some NoSQL databases need some time until committed changes are visible
			try {
				Thread.sleep( 200 );
			}
			catch (InterruptedException e) {
				throw new RuntimeException( e );
			}
		}
		return Assertions.fail( error );
	}
}
