/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.basic.test.mapping.primarykey.composite;

import jakarta.persistence.Embeddable;

@Embeddable
public record CompositeLongIdPrimaryKeyEntityId(Long id1, Long id2) {
}
