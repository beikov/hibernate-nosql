/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.nosql.test;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;
import org.hibernate.nosql.test.junit.Template;
import org.hibernate.nosql.test.junit.TemplateScope;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

@Template
@SessionFactory
@DomainModel(annotatedClasses = CrudTest.SimpleEntity.class)
public class CrudTest {

	@Test
	public void testInsert(TemplateScope scope) {
		scope.inTransaction( template -> {
			final SimpleEntity simpleEntity = simpleEntity();
			simpleEntity.id = "123";
			template.insert( simpleEntity );
		} );
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findExisting(template, SimpleEntity.class, "123" );
			assertNotNull( simpleEntity );
			assertEquals( "123", simpleEntity.id );
			assertEquals( "test", simpleEntity.theString );
			assertIsLikeInserted( simpleEntity );
		} );
	}

	@Test
	public void testUpdate(TemplateScope scope) {
		scope.inTransaction( template -> {
			final SimpleEntity simpleEntity = simpleEntity();
			simpleEntity.id = "456";
			template.insert( simpleEntity );
		} );
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findExisting(template, SimpleEntity.class, "456" );
			assertNotNull( simpleEntity );
			assertEquals( "456", simpleEntity.id );
			assertEquals( "test", simpleEntity.theString );
			assertIsLikeInserted( simpleEntity );

			simpleEntity.theString = "abc";
			template.update( simpleEntity );
		} );
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findUntil(template, SimpleEntity.class, "456", optional -> optional.isPresent() && optional.get().theString.equals( "abc" ) );
			assertNotNull( simpleEntity );
			assertEquals( "456", simpleEntity.id );
			assertEquals( "abc", simpleEntity.theString );
			assertIsLikeInserted( simpleEntity );
		} );
	}

	@Test
	public void testDelete(TemplateScope scope) {
		scope.inTransaction( template -> {
			final SimpleEntity simpleEntity = simpleEntity();
			simpleEntity.id = "789";
			template.insert( simpleEntity );
		} );
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findExisting(template, SimpleEntity.class, "789" );
			assertNotNull( simpleEntity );
			assertEquals( "789", simpleEntity.id );
			assertEquals( "test", simpleEntity.theString );
			assertIsLikeInserted( simpleEntity );
		} );
		scope.inTransaction( template -> {
			template.delete( SimpleEntity.class, "789" );
		} );
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findNonExisting( template, SimpleEntity.class, "789" );
			assertNull( simpleEntity );
		} );
	}

	@Test
	public void testFindNonExisting(TemplateScope scope) {
		scope.inTransaction( template -> {
			SimpleEntity simpleEntity = findNonExisting( template, SimpleEntity.class, "0" );
			assertNull( simpleEntity );
		} );
	}

	private <X> X findExisting(jakarta.nosql.Template template, Class<X> clazz, Object id) {
		return findUntil( template, clazz, id, Optional::isPresent );
	}

	private <X> X findNonExisting(jakarta.nosql.Template template, Class<X> clazz, Object id) {
		return findUntil( template, clazz, id, Optional::isEmpty );
	}

	private <X> X findUntil(jakarta.nosql.Template template, Class<X> clazz, Object id, Predicate<Optional<X>> acceptancePredicate) {
		int retries = 3;
		while ( 0 < retries-- ) {
			Optional<X> optional = template.find( clazz, id );
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
		return null;
	}

	private SimpleEntity simpleEntity() {
		final SimpleEntity simpleEntity = new SimpleEntity();
		simpleEntity.theBoolean = true;
		simpleEntity.theCharacter = 'A';
		simpleEntity.theByte = 1;
		simpleEntity.theShort = 2;
		simpleEntity.theInteger = 3;
		simpleEntity.theLong = 4L;
		simpleEntity.theFloat = 5.0f;
		simpleEntity.theDouble = 6.0d;
		simpleEntity.theString = "test";
		simpleEntity.theDate = LocalDate.of( 2000, 1, 1 );
		simpleEntity.theTime = LocalTime.of( 10, 11, 12 );
		simpleEntity.theDateTime = LocalDateTime.of( simpleEntity.theDate, simpleEntity.theTime );
		simpleEntity.theUuid = UUID.fromString( "53886a8a-7082-4879-b430-25cb94415be8" );
		return simpleEntity;
	}

	private void assertIsLikeInserted(SimpleEntity actual) {
		final SimpleEntity expected = simpleEntity();
		assertEquals( expected.theBoolean, actual.theBoolean );
		assertEquals( expected.theCharacter, actual.theCharacter );
		assertEquals( expected.theByte, actual.theByte );
		assertEquals( expected.theShort, actual.theShort );
		assertEquals( expected.theInteger, actual.theInteger );
		assertEquals( expected.theLong, actual.theLong );
		assertEquals( expected.theFloat, actual.theFloat );
		assertEquals( expected.theDouble, actual.theDouble );
//		assertEquals( expected.theString, actual.theString );
		assertEquals( expected.theDate, actual.theDate );
		assertEquals( expected.theTime, actual.theTime );
		assertEquals( expected.theDateTime, actual.theDateTime );
		assertEquals( expected.theUuid, actual.theUuid );
	}

	@Entity("SimpleEntity")
	public static class SimpleEntity {
		@Id("id")
		String id;
		@Column
		Boolean theBoolean;
		@Column
		Character theCharacter;
		@Column
		Byte theByte;
		@Column
		Short theShort;
		@Column
		Integer theInteger;
		@Column
		Long theLong;
		@Column
		Float theFloat;
		@Column
		Double theDouble;
		@Column
		String theString;
		@Column
		LocalDate theDate;
		@Column
		LocalTime theTime;
		@Column
		LocalDateTime theDateTime;
		@Column
		UUID theUuid;
	}
}
