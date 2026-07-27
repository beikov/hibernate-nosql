/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.nosql;

import org.hibernate.internal.build.AllowSysOut;

import static org.hibernate.internal.CoreMessageLogger.CORE_LOGGER;

/**
 * Information about the version of Hibernate NoSQL.
 *
 * @author Steve Ebersole
 */
public final class Version {

	private static final String VERSION = initVersion();

	private static String initVersion() {
		final String version = Version.class.getPackage().getImplementationVersion();
		return version != null ? version : "[WORKING]";
	}

	private Version() {
	}

	/**
	 * Access to the Hibernate NoSQL version.
	 *
	 * @return The Hibernate NoSQL version
	 */
	public static String getVersionString() {
		return VERSION;
	}

	/**
	 * Logs the Hibernate NoSQL version (using {@link #getVersionString()}) to the logging system.
	 */
	public static void logVersion() {
		CORE_LOGGER.version( getVersionString() );
	}

	/**
	 * Prints the Hibernate NoSQL version (using {@link #getVersionString()}) to SYSOUT.  Defined as the main-class in
	 * the hibernate-nosql-core jar
	 *
	 * @param args n/a
	 */
	@AllowSysOut
	public static void main(String[] args) {
		System.out.println( "Hibernate NoSQL version " + getVersionString() );
	}
}
