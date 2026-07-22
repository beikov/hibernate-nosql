/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus;

import org.hibernate.boot.registry.selector.spi.DialectSelector;
import org.hibernate.dialect.Dialect;

public class MilvusDialectSelector implements DialectSelector {

	@Override
	public Class<? extends Dialect> resolve(String name) {
		return "Milvus".equals(name) ? MilvusDialect.class : null;
	}
}
