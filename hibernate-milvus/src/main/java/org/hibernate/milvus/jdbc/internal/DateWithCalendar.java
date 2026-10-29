/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc.internal;

import java.util.Date;
import java.util.Calendar;

record DateWithCalendar(Date date, Calendar calendar) {
}
