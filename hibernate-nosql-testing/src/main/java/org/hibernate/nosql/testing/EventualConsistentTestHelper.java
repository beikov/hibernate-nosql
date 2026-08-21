/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.nosql.testing;

import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.function.Predicate;

public class EventualConsistentTestHelper {

	public static <X> X awaitExisting(SessionFactoryScope scope, Class<X> clazz, Object id) {
		return findUntil( scope, clazz, id, Optional::isPresent );
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
		int retries = 3;
		while ( 0 < retries-- ) {
			Optional<X> optional = Optional.ofNullable( scope.fromSession( session -> session.find( clazz, id ) ) );
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
		return Assertions.fail( "Could meet acceptance criteria for entity [" + clazz.getName() + "] with id " + id + " after 3 retries" );
	}
}
