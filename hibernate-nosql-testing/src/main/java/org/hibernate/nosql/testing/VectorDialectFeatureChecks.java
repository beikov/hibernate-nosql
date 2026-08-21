package org.hibernate.nosql.testing;

import org.hibernate.dialect.Dialect;
import org.hibernate.milvus.MilvusDialect;
import org.hibernate.testing.orm.junit.DialectFeatureCheck;

public final class VectorDialectFeatureChecks {
	private VectorDialectFeatureChecks() {
	}

	public static class SupportsExpressionsInSelectClause implements DialectFeatureCheck {
		@Override
		public boolean apply(Dialect dialect) {
			// Milvus only supports selecting column references and aggregate functions
			return !(dialect instanceof MilvusDialect);
		}
	}

	public static class SupportsBinaryOperationsOnNonBinaryVector implements DialectFeatureCheck {
		@Override
		public boolean apply(Dialect dialect) {
			// Most databases only support the binary operations hamming and jaccard distance on binary vectors
			return !(dialect instanceof MilvusDialect);
		}
	}

}
