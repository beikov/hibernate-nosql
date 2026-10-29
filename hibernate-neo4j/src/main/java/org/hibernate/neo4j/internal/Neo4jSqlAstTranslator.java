/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.neo4j.internal;

import org.hibernate.AssertionFailure;
import org.hibernate.LockMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.MathHelper;
import org.hibernate.internal.util.collections.Stack;
import org.hibernate.metamodel.mapping.ModelPartContainer;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.query.sqm.sql.internal.SqmParameterInterpretation;
import org.hibernate.query.sqm.sql.internal.SqmPathInterpretation;
import org.hibernate.query.sqm.tuple.internal.AnonymousTupleTableGroupProducer;
import org.hibernate.sql.ast.Clause;
import org.hibernate.sql.ast.SqlAstJoinType;
import org.hibernate.sql.ast.SqlAstNodeRenderingMode;
import org.hibernate.sql.ast.internal.TableGroupHelper;
import org.hibernate.sql.ast.spi.AbstractSqlAstTranslator;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.ast.tree.delete.DeleteStatement;
import org.hibernate.sql.ast.tree.expression.ColumnReference;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.hibernate.sql.ast.tree.expression.Literal;
import org.hibernate.sql.ast.tree.expression.QueryLiteral;
import org.hibernate.sql.ast.tree.expression.SqlTuple;
import org.hibernate.sql.ast.tree.expression.SqlTupleContainer;
import org.hibernate.sql.ast.tree.from.FromClause;
import org.hibernate.sql.ast.tree.from.NamedTableReference;
import org.hibernate.sql.ast.tree.from.TableGroup;
import org.hibernate.sql.ast.tree.from.TableGroupJoin;
import org.hibernate.sql.ast.tree.from.TableReference;
import org.hibernate.sql.ast.tree.from.TableReferenceJoin;
import org.hibernate.sql.ast.tree.insert.InsertSelectStatement;
import org.hibernate.sql.ast.tree.predicate.BetweenPredicate;
import org.hibernate.sql.ast.tree.predicate.BooleanExpressionPredicate;
import org.hibernate.sql.ast.tree.predicate.InListPredicate;
import org.hibernate.sql.ast.tree.predicate.Predicate;
import org.hibernate.sql.ast.tree.select.QuerySpec;
import org.hibernate.sql.ast.tree.select.SelectClause;
import org.hibernate.sql.ast.tree.select.SelectStatement;
import org.hibernate.sql.ast.tree.update.Assignment;
import org.hibernate.sql.ast.tree.update.UpdateStatement;
import org.hibernate.sql.exec.spi.JdbcOperation;
import org.hibernate.sql.model.MutationOperation;
import org.hibernate.sql.model.ast.RestrictedTableMutation;
import org.hibernate.sql.model.internal.TableDeleteStandard;
import org.hibernate.sql.model.internal.TableInsertCustomSql;
import org.hibernate.sql.model.internal.TableInsertStandard;
import org.hibernate.sql.model.internal.TableUpdateStandard;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static org.hibernate.internal.util.StringHelper.isBlank;
import static org.hibernate.sql.ast.tree.expression.SqlTupleContainer.getSqlTuple;

public class Neo4jSqlAstTranslator<T extends JdbcOperation> extends AbstractSqlAstTranslator<T> {

	public Neo4jSqlAstTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
		super( sessionFactory, statement );
	}

	@Override
	public void visitQueryClauses(QuerySpec querySpec) {
		visitFromClause( querySpec.getFromClause() ); // match
		if ( hasWhere( querySpec.getWhereClauseRestrictions() ) && querySpec.getFromClause().hasJoins() ) {
			// A previous join might have scoped a where clause to that match, so we need to reset the scope
			appendSql( " with *" );
		}
		visitWhereClause( querySpec.getWhereClauseRestrictions() );
		super.visitForUpdateClause( querySpec );
		visitSelectClause( querySpec.getSelectClause() ); // return
		visitGroupByClause( querySpec, getDialect().getGroupBySelectItemReferenceStrategy() );
		visitHavingClause( querySpec.getHavingClauseRestrictions() );
		visitOrderBy( querySpec.getSortSpecifications() );
		visitOffsetFetchClause( querySpec );
	}

	@Override
	public void visitFromClause(FromClause fromClause) {
		if ( fromClause == null || fromClause.getRoots().isEmpty() ) {
//			throw new UnsupportedOperationException( "Neo4j doesn't support an empty from clause" );
		}
		else {
			appendSql( "match " );
			renderFromClauseSpaces( fromClause );
		}
	}

	@Override
	protected void renderFromClauseSpaces(FromClause fromClause) {
		try {
			getCurrentClauseStack().push( Clause.FROM );
			String separator = NO_SEPARATOR;
			for ( TableGroup root : fromClause.getRoots() ) {
				separator = renderFromClauseRoot( root, separator );
			}
		}
		finally {
			getCurrentClauseStack().pop();
		}
	}

	private String renderFromClauseRoot(TableGroup root, String separator) {
		if ( root.isVirtual() ) {
			for ( TableGroupJoin tableGroupJoin : root.getTableGroupJoins() ) {
				addAdditionalWherePredicate( tableGroupJoin.getPredicate() );
				renderFromClauseRoot( tableGroupJoin.getJoinedGroup(), separator );
			}
			for ( TableGroupJoin tableGroupJoin : root.getNestedTableGroupJoins() ) {
				addAdditionalWherePredicate( tableGroupJoin.getPredicate() );
				renderFromClauseRoot( tableGroupJoin.getJoinedGroup(), separator );
			}
		}
		else if ( root.isInitialized() ) {
			appendSql( separator );
			renderRootTableGroup( root, null );
		}
		return ",";
	}

	@Override
	protected boolean renderNamedTableReference(NamedTableReference tableReference, LockMode lockMode) {
		appendSql( '(' );
		renderTableReferenceIdentificationVariable( tableReference );
		appendSql( ':' );
		appendSql( tableReference.getTableExpression() );
		appendSql( ')' );
		registerAffectedTable( tableReference );
		return false;
	}

	@Override
	protected void visitForUpdateClause(QuerySpec querySpec) {
		// No-op, handled in visitQueryClauses
	}

	@Override
	public void visitSelectClause(SelectClause selectClause) {
		getCurrentClauseStack().push( Clause.SELECT );

		try {
			appendSql( " return " );
			if ( selectClause.isDistinct() ) {
				appendSql( "distinct " );
			}
			visitSqlSelections( selectClause );
			renderVirtualSelections( selectClause );
		}
		finally {
			getCurrentClauseStack().pop();
		}
	}

	@Override
	protected void renderTableGroupJoin(TableGroupJoin tableGroupJoin, List<TableGroupJoin> tableGroupJoinCollector) {
		appendSql( WHITESPACE );
		if ( tableGroupJoin.getJoinType() != SqlAstJoinType.INNER ) {
			appendSql( "optional " );
		}
		appendSql( "match " );

		final Predicate predicate;
		if ( tableGroupJoin.getPredicate() == null ) {
			if ( tableGroupJoin.getJoinType() == SqlAstJoinType.CROSS ) {
				predicate = null;
			}
			else {
				predicate = new BooleanExpressionPredicate( new QueryLiteral<>( true, getBooleanType() ) );
			}
		}
		else {
			predicate = tableGroupJoin.getPredicate();
		}
		if ( predicate != null && !predicate.isEmpty() ) {
			renderJoinedTableGroup( tableGroupJoin, predicate, tableGroupJoinCollector );
		}
		else {
			renderJoinedTableGroup( tableGroupJoin, null, tableGroupJoinCollector );
		}
	}

	@Override
	protected void renderJoinedTableGroup(TableGroupJoin tableGroupJoin, Predicate predicate, List<TableGroupJoin> tableGroupJoinCollector) {
		final LockMode lockModeToApply = determineJoinedTableGroupLockMode( tableGroupJoin );
		var tableGroup = tableGroupJoin.getJoinedGroup();

		final boolean realTableGroup;
		int swappedJoinIndex = -1;
		boolean forceLeftJoin = false;
		if ( tableGroup.isRealTableGroup() ) {
			if ( hasNestedTableGroupsToRender( tableGroup.getNestedTableGroupJoins() ) ) {
				// If there are nested table groups, we need to render a real table group
				realTableGroup = true;
			}
			else {
				// Determine the reference join indexes of the table reference used in the predicate
				final int referenceJoinIndexForPredicateSwap = TableGroupHelper.findReferenceJoinForPredicateSwap(
						tableGroup,
						predicate
				);
				if ( referenceJoinIndexForPredicateSwap == TableGroupHelper.REAL_TABLE_GROUP_REQUIRED ) {
					// Means that real table group rendering is necessary
					realTableGroup = true;
				}
				else if ( referenceJoinIndexForPredicateSwap == TableGroupHelper.NO_TABLE_GROUP_REQUIRED ) {
					// Means that no swap is necessary to avoid the table group rendering
					realTableGroup = false;
					forceLeftJoin = !tableGroup.canUseInnerJoins();
				}
				else {
					// Means that real table group rendering can be avoided if the primary table reference is swapped
					// with the table reference join at the given index
					realTableGroup = false;
					forceLeftJoin = !tableGroup.canUseInnerJoins();
					swappedJoinIndex = referenceJoinIndexForPredicateSwap;

					// Render the table reference of the table reference join first
					final TableReferenceJoin tableReferenceJoin = tableGroup.getTableReferenceJoins()
							.get( swappedJoinIndex );
					renderNamedTableReference( tableReferenceJoin.getJoinedTableReference(), LockMode.NONE );
					// along with the predicate for the table group
					renderJoinPredicate( predicate );

					// Then render the join syntax and fall through to rendering the primary table reference
					appendSql( WHITESPACE );
					if ( !tableGroup.canUseInnerJoins() || tableReferenceJoin.getJoinType() != SqlAstJoinType.LEFT ) {
						appendSql( "optional " );
					}
					appendSql( "match " );
				}
			}
		}
		else {
			realTableGroup = false;
		}
		if ( realTableGroup ) {
			appendSql( OPEN_PARENTHESIS );
		}

		renderPrimaryTableReference( tableGroup, lockModeToApply );
		final List<TableGroupJoin> tableGroupJoins;

		if ( realTableGroup ) {
			// For real table groups, we collect all normal table group joins within that table group
			// The purpose of that is to render them in-order outside of the group/parenthesis
			// This is necessary for at least Derby but is also a lot easier to read
			renderTableReferenceJoins( tableGroup, lockModeToApply );
			if ( tableGroupJoinCollector == null ) {
				tableGroupJoins = new ArrayList<>();
				processNestedTableGroupJoins( tableGroup, tableGroupJoins );
			}
			else {
				tableGroupJoins = null;
				processNestedTableGroupJoins( tableGroup, tableGroupJoinCollector );
			}
			appendSql( CLOSE_PARENTHESIS );
		}
		else {
			tableGroupJoins = null;
		}

		// Predicate was already rendered when swappedJoinIndex is not equal to -1
		if ( predicate != null && swappedJoinIndex == -1 ) {
			renderJoinPredicate( predicate );
		}
		if ( tableGroup.isLateral() && !getDialect().supportsLateral() ) {
			final Predicate lateralEmulationPredicate = determineLateralEmulationPredicate( tableGroup );
			if ( lateralEmulationPredicate != null ) {
				if ( predicate == null ) {
					renderJoinPredicate( lateralEmulationPredicate );
				}
				else {
					appendSql( " and " );
					lateralEmulationPredicate.accept( this );
				}
			}
		}

		if ( !realTableGroup ) {
			renderTableReferenceJoins( tableGroup, lockModeToApply, swappedJoinIndex, forceLeftJoin );
			processNestedTableGroupJoins( tableGroup, tableGroupJoinCollector );
		}
		if ( tableGroupJoinCollector != null ) {
			tableGroupJoinCollector.addAll( tableGroup.getTableGroupJoins() );
		}
		else {
			if ( tableGroupJoins != null ) {
				for ( TableGroupJoin join : tableGroupJoins ) {
					processTableGroupJoin( join, null );
				}
			}
			processTableGroupJoins( tableGroup );
		}

		ModelPartContainer modelPart = tableGroup.getModelPart();
		if ( modelPart instanceof EntityPersister persister ) {
			final String[] querySpaces = (String[]) persister.getQuerySpaces();
			for ( String querySpace : querySpaces ) {
				registerAffectedTable( querySpace );
			}
		}
	}

	private QuerySpec getLockingTarget() {
		// todo: temporary, until we add the getter to ORM
		try {
			Field lockingTarget = AbstractSqlAstTranslator.class.getDeclaredField( "lockingTarget" );
			lockingTarget.setAccessible( true );
			return (QuerySpec) lockingTarget.get( this );
		}
		catch (Exception e) {
			throw new RuntimeException( e );
		}
	}

	private LockMode determineJoinedTableGroupLockMode(TableGroupJoin join) {
		if ( getLockingClauseStrategy() != null ) {
			if ( getCurrentQueryPart() == getLockingTarget() ) {
				if ( getLockingClauseStrategy().registerJoin( join ) ) {
					return getEffectiveLockMode( join.getJoinedGroup().getSourceAlias() );
				}
			}
		}

		return LockMode.NONE;
	}

	@Override
	protected void renderTableReferenceJoins(TableGroup tableGroup, LockMode lockMode, int swappedJoinIndex, boolean forceLeftJoin) {
		final List<TableReferenceJoin> joins = tableGroup.getTableReferenceJoins();
		if ( joins == null || joins.isEmpty() ) {
			return;
		}

		if ( swappedJoinIndex != -1 ) {
			// Finish the join against the primary table reference after the swap
			final TableReferenceJoin swappedJoin = joins.get( swappedJoinIndex );
			renderJoinPredicate( swappedJoin.getPredicate() );
		}

		for ( int i = 0; i < joins.size(); i++ ) {
			// Skip the swapped join since it was already rendered
			if ( swappedJoinIndex != i ) {
				final TableReferenceJoin tableJoin = joins.get( i );
				appendSql( WHITESPACE );
				if ( forceLeftJoin || tableJoin.getJoinType() != SqlAstJoinType.INNER ) {
					append( "optional " );
				}
				appendSql( "match " );

				renderNamedTableReference( tableJoin.getJoinedTableReference(), lockMode );

				renderJoinPredicate( tableJoin.getPredicate() );
			}
		}
	}

	private void renderJoinPredicate(Predicate predicate) {
		if ( predicate != null && !predicate.isEmpty() ) {
			appendSql( " where " );
			predicate.accept( this );
		}
	}

	protected void visitHavingClause(Predicate havingClauseRestrictions) {
		if ( havingClauseRestrictions != null && !havingClauseRestrictions.isEmpty() ) {
			appendSql( " where " );

			getClauseStack().push( Clause.HAVING );
			try {
				havingClauseRestrictions.accept( this );
			}
			finally {
				getClauseStack().pop();
			}
		}
	}

	// ~ MUTATION OPERATIONS

	@Override
	public void visitStandardTableInsert(TableInsertStandard tableInsert) {
		getCurrentClauseStack().push( Clause.INSERT );
		appendSql( "create " );
		appendSql( OPEN_PARENTHESIS );

		appendSql( "n:" );
		appendSql( tableInsert.getMutatingTable().getTableName() );
		appendSql( ' ' );

		getCurrentClauseStack().push( Clause.VALUES );
		tableInsert.forEachValueBinding( (columnPosition, columnValueBinding) -> {
			if ( columnPosition == 0 ) {
				appendSql( '{' );
			}
			else {
				appendSql( ',' );
			}
			appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
			appendSql( ':' );
			columnValueBinding.getValueExpression().accept( this );
		} );
		appendSql( "})" );

		if ( tableInsert.getNumberOfReturningColumns() > 0 ) {
			visitReturningColumns( tableInsert.getReturningColumns(), "n" );
		}

		getCurrentClauseStack().pop();
	}

	@Override
	public void visitInsertStatement(InsertSelectStatement statement) {
		throw new UnsupportedOperationException( "Neo4j does not support insert-select statements" );
	}

	@Override
	public void visitCustomTableInsert(TableInsertCustomSql tableInsert) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitStandardTableUpdate(TableUpdateStandard tableUpdate) {
		getCurrentClauseStack().push( Clause.UPDATE );
		try {
			visitTableUpdate( tableUpdate, tableUpdate.getWhereFragment() );
			if ( tableUpdate.getNumberOfReturningColumns() > 0 ) {
				visitReturningColumns( tableUpdate.getReturningColumns(), "n" );
			}
		}
		finally {
			getCurrentClauseStack().pop();
		}
	}

	private void visitTableUpdate(RestrictedTableMutation<? extends MutationOperation> tableUpdate, String whereFragment) {
		applySqlComment( tableUpdate.getMutationComment() );

		appendSql( "match(n:" );
		appendSql( tableUpdate.getMutatingTable().getTableName() );
		registerAffectedTable( tableUpdate.getMutatingTable().getTableName() );
		appendSql( ')' );

		getCurrentClauseStack().push( Clause.WHERE );
		try {
			appendSql( " where" );
			tableUpdate.forEachKeyBinding( (position, columnValueBinding) -> {
				if ( position == 0 ) {
					appendSql( ' ' );
				}
				else {
					appendSql( " and " );
				}
				appendSql( "n." );
				appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
				appendSql( '=' );
				columnValueBinding.getValueExpression().accept( this );
			} );

			if ( tableUpdate.getNumberOfOptimisticLockBindings() > 0 ) {
				tableUpdate.forEachOptimisticLockBinding( (position, columnValueBinding) -> {
					appendSql( " and n." );
					appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
					if ( columnValueBinding.getValueExpression() == null
						|| columnValueBinding.getValueExpression().getFragment() == null ) {
						appendSql( " is null" );
					}
					else {
						appendSql( "=" );
						columnValueBinding.getValueExpression().accept( this );
					}
				} );
			}

			if ( !isBlank( whereFragment ) ) {
				appendSql( " and " + whereFragment );
			}
		}
		finally {
			getCurrentClauseStack().pop();
		}

		getCurrentClauseStack().push( Clause.SET );
		try {
			appendSql( " set" );
			tableUpdate.forEachValueBinding( (columnPosition, columnValueBinding) -> {
				if ( columnPosition == 0 ) {
					appendSql( ' ' );
				}
				else {
					appendSql( ',' );
				}
				appendSql( "n." );
				appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
				appendSql( '=' );
				columnValueBinding.getValueExpression().accept( this );
			} );
		}
		finally {
			getCurrentClauseStack().pop();
		}
	}

	@Override
	protected void visitUpdateStatementOnly(UpdateStatement statement) {
		renderUpdateClause( statement );
		visitWhereClause( statement.getRestriction() );
		renderSetClause( statement.getAssignments() );
		renderFromClauseAfterUpdateSet( statement );
		visitReturningColumns( statement.getReturningColumns(), getTableAlias( statement.getTargetTable() ) );
	}

	static String getTableAlias(TableReference tableReference) {
		return tableReference.getIdentificationVariable() != null
				? tableReference.getIdentificationVariable()
				: tableReference.getTableId();
	}

	@Override
	protected void renderUpdateClause(UpdateStatement updateStatement) {
		appendSql( "match (" );
		final Stack<Clause> clauseStack = getClauseStack();
		try {
			clauseStack.push( Clause.UPDATE );
			renderDmlTargetTableExpression( updateStatement.getTargetTable() );
			appendSql( ')' );
		}
		finally {
			clauseStack.pop();
		}
	}

	@Override
	protected void renderDmlTargetTableExpression(NamedTableReference tableReference) {
		renderTableReferenceIdentificationVariable( tableReference );
		appendSql( ':' );
		super.renderDmlTargetTableExpression( tableReference );
	}

	@Override
	protected void renderTableReferenceIdentificationVariable(TableReference tableReference) {
		appendSql( getTableAlias( tableReference ) );
	}

	@Override
	protected void visitSetAssignment(Assignment assignment) {
		if ( assignment.getAssignable() instanceof SqmPathInterpretation<?> sqmPathInterpretation ) {
			final String affectedTableName = sqmPathInterpretation.getAffectedTableName();
			if ( affectedTableName != null ) {
				addAffectedTableName( affectedTableName );
			}
		}
		final List<ColumnReference> columnReferences = assignment.getAssignable().getColumnReferences();
		final Expression assignedValue = assignment.getAssignedValue();
		if ( columnReferences.size() == 1 ) {
			columnReferences.get( 0 ).appendColumnForWrite( this );
			appendSql( '=' );
			final SqlTuple sqlTuple = getSqlTuple( assignedValue );
			if ( sqlTuple != null ) {
				assert sqlTuple.getExpressions().size() == 1;
				sqlTuple.getExpressions().get( 0 ).accept( this );
			}
			else {
				assignedValue.accept( this );
			}
		}
		else if ( assignedValue instanceof SelectStatement ) {
			// todo neo4j : we should render a separate match clause before the set
			throw new UnsupportedOperationException(
					"Neo4j does not support subquery assignments in update statements" );
		}
		else if ( assignedValue instanceof SqlTupleContainer ) {
			throw new UnsupportedOperationException( "Not yet implemented" );
		}
		else {
			throw new AssertionFailure( "Unexpected assigned value" );
		}
	}

	@Override
	protected void renderFromClauseAfterUpdateSet(UpdateStatement statement) {
		renderFromClauseJoiningDmlTargetReference( statement );
	}

	protected void applySqlComment(String comment) {

		if ( getSessionFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
			if ( comment != null ) {
				appendSql( "/* " );
				appendSql( Dialect.escapeComment( comment ) );
				appendSql( " */" );
			}
		}
	}

	@Override
	public void visitStandardTableDelete(TableDeleteStandard tableDelete) {
		getCurrentClauseStack().push( Clause.DELETE );
		try {
			applySqlComment( tableDelete.getMutationComment() );

			appendSql( "match(n:" );
			appendSql( tableDelete.getMutatingTable().getTableName() );
			appendSql( ')' );
			registerAffectedTable( tableDelete.getMutatingTable().getTableName() );

			getCurrentClauseStack().push( Clause.WHERE );
			try {
				appendSql( " where " );

				tableDelete.forEachKeyBinding( (columnPosition, columnValueBinding) -> {
					appendSql( "n." );
					appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
					appendSql( "=" );
					columnValueBinding.getValueExpression().accept( this );

					if ( columnPosition < tableDelete.getNumberOfKeyBindings() - 1 ) {
						appendSql( " and " );
					}
				} );

				if ( tableDelete.getNumberOfOptimisticLockBindings() > 0 ) {
					appendSql( " and " );

					tableDelete.forEachOptimisticLockBinding( (columnPosition, columnValueBinding) -> {
						appendSql( "n." );
						appendSql( columnValueBinding.getColumnReference().getColumnExpression() );
						if ( columnValueBinding.getValueExpression() == null ) {
							appendSql( " is null" );
						}
						else {
							appendSql( "=" );
							columnValueBinding.getValueExpression().accept( this );
						}

						if ( columnPosition < tableDelete.getNumberOfOptimisticLockBindings() - 1 ) {
							appendSql( " and " );
						}
					} );
				}

				if ( tableDelete.getWhereFragment() != null ) {
					appendSql( " and (" );
					appendSql( tableDelete.getWhereFragment() );
					appendSql( ")" );
				}
			}
			finally {
				getCurrentClauseStack().pop();
			}

			appendSql( " delete n" );
		}
		finally {
			getCurrentClauseStack().pop();
		}
	}

	@Override
	protected void visitDeleteStatementOnly(DeleteStatement statement) {
		renderDeleteClause( statement );
		visitWhereClause( statement.getRestriction() );
		final String tableAlias = getTableAlias( statement.getTargetTable() );
		if ( !statement.getReturningColumns().isEmpty() ) {
			appendSql( String.format( " with %s, properties(%s) as returning_properties", tableAlias, tableAlias ) );
		}
		appendSql( " delete " + tableAlias );
		visitReturningColumns( statement.getReturningColumns(), "returning_properties" );
	}

	@Override
	protected void renderDeleteClause(DeleteStatement statement) {
		appendSql( "match (" );
		final Stack<Clause> clauseStack = getClauseStack();
		try {
			clauseStack.push( Clause.DELETE );
			renderDmlTargetTableExpression( statement.getTargetTable() );
			appendSql( ')' );
		}
		finally {
			clauseStack.pop();
		}
	}

	protected void visitReturningColumns(List<ColumnReference> returningColumns, String tableAlias) {
		if ( !returningColumns.isEmpty() ) {
			appendSql( " return " );
			String separator = "";
			for ( ColumnReference columnReference : returningColumns ) {
				appendSql( separator );
				appendSql( tableAlias + "." + columnReference.getColumnExpression() );
				separator = COMMA_SEPARATOR;
			}
		}
	}

	@Override
	public void renderNamedSetReturningFunction(String functionName, List<? extends SqlAstNode> sqlAstArguments, AnonymousTupleTableGroupProducer tupleType, String tableIdentifierVariable, SqlAstNodeRenderingMode argumentRenderingMode) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void visitBetweenPredicate(BetweenPredicate betweenPredicate) {
		appendSql( '(' );
		betweenPredicate.getUpperBound().accept( this );
		appendSql( betweenPredicate.isNegated() ? " < " : " >= " );
		betweenPredicate.getExpression().accept( this );
		if ( betweenPredicate.isNegated() ) {
			appendSql( " or " );
			betweenPredicate.getExpression().accept( this );
		}
		appendSql( betweenPredicate.isNegated() ? " < " : " >= " );
		betweenPredicate.getLowerBound().accept( this );
		appendSql( ')' );
	}

	@Override
	public void visitTuple(SqlTuple tuple) {
		// A tuple in a values clause of an insert-select statement must be unwrapped,
		// since the assignment target is also unwrapped to the individual column references
		final boolean wrap = getClauseStack().getCurrent() != Clause.VALUES;
		if ( wrap ) {
			appendSql( '[' );
		}

		renderCommaSeparated( tuple.getExpressions() );

		if ( wrap ) {
			appendSql( ']' );
		}
	}

	@Override
	public void visitInListPredicate(InListPredicate inListPredicate) {
		final List<Expression> listExpressions = inListPredicate.getListExpressions();
		if ( listExpressions.isEmpty() ) {
			appendSql( "1=" + ( inListPredicate.isNegated() ? "1" : "0" ) );
			return;
		}

		Function<Expression, Expression> itemAccessor = Function.identity();
		final SqlTuple lhsTuple;
		if ( ( lhsTuple = getSqlTuple( inListPredicate.getTestExpression() ) ) != null ) {
			if ( lhsTuple.getExpressions().size() == 1 ) {
				// Special case for tuples with arity 1 as any DBMS supports scalar IN predicates
				if ( getSqlTuple( listExpressions.get( 0 ) ) != null ) {
					itemAccessor = listExpression ->
							getSqlTuple( listExpression )
									.getExpressions().get( 0 );
				}
			}
		}

		int bindValueCount = listExpressions.size();
		int bindValueCountWithPadding = bindValueCount;

		int inExprLimit = getDialect().getInExpressionCountLimit();

		if ( getSessionFactory().getSessionFactoryOptions().inClauseParameterPaddingEnabled() ) {
			bindValueCountWithPadding = addPadding( bindValueCount, inExprLimit );
		}

		final boolean parenthesis = !inListPredicate.isNegated()
				&& inExprLimit > 0 && listExpressions.size() > inExprLimit;
		if ( parenthesis ) {
			appendSql( OPEN_PARENTHESIS );
		}

		inListPredicate.getTestExpression().accept( this );
		if ( inListPredicate.isNegated() ) {
			appendSql( " not" );
		}
		appendSql( " in [" );
		String separator = NO_SEPARATOR;

		final Iterator<Expression> iterator = listExpressions.iterator();
		Expression listExpression = null;
		int clauseItemNumber = 0;
		for ( int i = 0; i < bindValueCountWithPadding; i++, clauseItemNumber++ ) {
			if ( inExprLimit > 0 && inExprLimit == clauseItemNumber ) {
				clauseItemNumber = 0;
				appendInClauseSeparator( inListPredicate );
				separator = NO_SEPARATOR;
			}

			if ( iterator.hasNext() ) { // If the iterator is exhausted, reuse the last expression for padding.
				listExpression = itemAccessor.apply( iterator.next() );
			}
			// The only way for expression to be null is if listExpressions is empty,
			// but if that is the case the code takes an early exit.
			assert listExpression != null;

			appendSql( separator );
			listExpression.accept( this );
			separator = COMMA_SEPARATOR;

			// If we encounter an expression that is not a parameter or literal, we reset the inExprLimit and
			// bindValueMaxCount and just render through the in list expressions as they are without padding/splitting
			if ( !(listExpression instanceof JdbcParameter || listExpression instanceof SqmParameterInterpretation || listExpression instanceof Literal) ) {
				inExprLimit = 0;
				bindValueCountWithPadding = bindValueCount;
			}
		}

		appendSql( ']' );
		if ( parenthesis ) {
			appendSql( CLOSE_PARENTHESIS );
		}
	}

	private void appendInClauseSeparator(InListPredicate inListPredicate) {
		appendSql( ']' );
		appendSql( inListPredicate.isNegated() ? " and " : " or " );
		inListPredicate.getTestExpression().accept( this );
		if ( inListPredicate.isNegated() ) {
			appendSql( " not" );
		}
		appendSql( " in [" );
	}

	private static int addPadding(int bindValueCount, int inExprLimit) {
		final int ceilingPowerOfTwo = MathHelper.ceilingPowerOfTwo( bindValueCount );
		if ( inExprLimit <= 0 || ceilingPowerOfTwo <= inExprLimit ) {
			return ceilingPowerOfTwo;
		}
		else {
			int numberOfInClauses = MathHelper.divideRoundingUp( bindValueCount, inExprLimit );
			int numberOfInClausesWithPadding = MathHelper.ceilingPowerOfTwo( numberOfInClauses );
			return numberOfInClausesWithPadding * inExprLimit;
		}
	}
}
