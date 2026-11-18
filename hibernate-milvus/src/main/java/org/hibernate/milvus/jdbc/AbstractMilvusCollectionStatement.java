/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.milvus.jdbc;

public abstract class AbstractMilvusCollectionStatement {
	private String collectionName;
	/**
	 * If set, the statement can't be executed and executing it fails with the given reason.
	 * This allows to translate statements that Milvus doesn't support but that Hibernate ORM creates eagerly.
	 */
	private String unsupportedReason;

	public AbstractMilvusCollectionStatement() {
	}

	public AbstractMilvusCollectionStatement(AbstractMilvusCollectionStatement original) {
		this.collectionName = original.getCollectionName();
		this.unsupportedReason = original.getUnsupportedReason();
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public String getUnsupportedReason() {
		return unsupportedReason;
	}

	public void setUnsupportedReason(String unsupportedReason) {
		this.unsupportedReason = unsupportedReason;
	}

}
