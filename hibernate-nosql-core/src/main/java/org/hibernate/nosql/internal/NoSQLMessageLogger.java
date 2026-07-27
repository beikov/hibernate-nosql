/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.nosql.internal;

import org.hibernate.Internal;
import org.hibernate.internal.log.SubSystemLogging;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

import java.lang.invoke.MethodHandles;
import java.util.Locale;

import static org.jboss.logging.Logger.Level.INFO;

/**
 * Miscellaneous logging related to Hibernate NoSQL.
 */
@SubSystemLogging(
		name = NoSQLMessageLogger.NAME,
		description = "Miscellaneous logging related to Hibernate NoSQL"
)
@MessageLogger(projectCode = "HNSQL")
@ValidIdRange(min = 1, max = 8000)
@Internal
public interface NoSQLMessageLogger extends BasicLogger {

	String NAME = "org.hibernate.nosql";

	NoSQLMessageLogger INSTANCE = Logger.getMessageLogger( MethodHandles.lookup(), NoSQLMessageLogger.class, NAME, Locale.ROOT );

	@LogMessage(level = INFO)
	@Message(value = "Hibernate NoSQL core version %s", id = 1)
	void version(String versionString);
}
