/*
 * Copyright 2000-2016 Holon TDCN.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.internal.query.QueryFilterVisitor;
import com.holonplatform.core.internal.query.QueryFilterVisitor.VisitableQueryFilter;
import com.holonplatform.core.internal.query.QueryUtils;
import com.holonplatform.core.internal.query.filter.AndFilter;
import com.holonplatform.core.internal.query.filter.BetweenFilter;
import com.holonplatform.core.internal.query.filter.EqualFilter;
import com.holonplatform.core.internal.query.filter.GreaterFilter;
import com.holonplatform.core.internal.query.filter.InFilter;
import com.holonplatform.core.internal.query.filter.LessFilter;
import com.holonplatform.core.internal.query.filter.NotEqualFilter;
import com.holonplatform.core.internal.query.filter.NotFilter;
import com.holonplatform.core.internal.query.filter.NotInFilter;
import com.holonplatform.core.internal.query.filter.NotNullFilter;
import com.holonplatform.core.internal.query.filter.NullFilter;
import com.holonplatform.core.internal.query.filter.OrFilter;
import com.holonplatform.core.internal.query.filter.StringMatchFilter;
import com.holonplatform.core.query.QueryExpression;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

/**
 * QueryDsl {@link VisitableQueryFilter} expression resolver.
 * 
 * @since 5.0.0
 */
@Priority(Integer.MAX_VALUE - 12)
public enum QueryDslVisitableQueryFilterResolver
		implements ExpressionResolver<VisitableQueryFilter, PredicateExpression>,
		QueryFilterVisitor<PredicateExpression, QueryDslResolutionContext> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends VisitableQueryFilter> getExpressionType() {
		return VisitableQueryFilter.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends PredicateExpression> getResolvedType() {
		return PredicateExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	public Optional<PredicateExpression> resolve(VisitableQueryFilter expression, ResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve using visitor
		return Optional.ofNullable(expression.accept(this, QueryDslResolutionContext.checkContext(context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NullFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(NullFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions.booleanOperation(Ops.IS_NULL, resolve(filter.getLeftOperand(), context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotNullFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(NotNullFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions.booleanOperation(Ops.IS_NOT_NULL, resolve(filter.getLeftOperand(), context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * EqualFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(EqualFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(Ops.EQ,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotEqualFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(NotEqualFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(Ops.NE,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * GreaterFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(GreaterFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(filter.isIncludeEquals() ? Ops.GOE : Ops.GT,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * LessFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(LessFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(filter.isIncludeEquals() ? Ops.LOE : Ops.LT,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * InFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(InFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(Ops.IN,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotInFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(NotInFilter<T> filter, QueryDslResolutionContext context) {
		return PredicateExpression
				.create(Expressions
						.booleanOperation(Ops.NOT_IN,
								resolve(filter.getLeftOperand(), context), resolve(
										filter.getRightOperand()
												.orElseThrow(() -> new InvalidExpressionException(
														"Missing right operand in filter [" + filter + "]")),
										context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * BetweenFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(BetweenFilter<T> filter, QueryDslResolutionContext context) {
		final Expression<?> left = resolve(filter.getLeftOperand(), context);
		final Expression<?> from = resolve(
				QueryUtils.asConstantExpression(filter.getLeftOperand(), filter.getFromValue()), context);
		final Expression<?> to = resolve(QueryUtils.asConstantExpression(filter.getLeftOperand(), filter.getToValue()),
				context);
		return PredicateExpression.create(Expressions.booleanOperation(Ops.BETWEEN, left, from, to));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * StringMatchFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(StringMatchFilter filter, QueryDslResolutionContext context) {
		final Expression<?> left = resolve(filter.getLeftOperand(), context);

		final QueryExpression<? super String> rightExpression = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));

		final Object value = QueryUtils.getConstantExpressionValue(rightExpression);

		String pattern = (value != null) ? value.toString() : "";

		// add wildcards
		switch (filter.getMatchMode()) {
		case CONTAINS:
			pattern = "%" + pattern + "%";
			break;
		case ENDS_WITH:
			pattern = "%" + pattern;
			break;
		case STARTS_WITH:
			pattern = pattern + "%";
			break;
		default:
			break;
		}

		if (filter.isIgnoreCase()) {
			return PredicateExpression
					.create(Expressions.booleanOperation(Ops.LIKE_IC, left, ConstantImpl.create(pattern)));
		}

		return PredicateExpression.create(Expressions.booleanOperation(Ops.LIKE, left, ConstantImpl.create(pattern)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * AndFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(AndFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression.create(ExpressionUtils.allOf(resolveFilterList(filter.getComposition(), context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * OrFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(OrFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression.create(ExpressionUtils.anyOf(resolveFilterList(filter.getComposition(), context)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(NotFilter filter, QueryDslResolutionContext context) {
		PredicateExpression prd = context.resolve(filter.getComposition().get(0), PredicateExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException(
						"Failed to resolve QueryFilter [" + filter.getComposition().get(0) + "]"));
		prd.validate();
		return PredicateExpression.create(prd.getPredicate().not());
	}

	private static Expression<?> resolve(com.holonplatform.core.Expression expression,
			QueryDslResolutionContext context) {
		QueryDslExpression<?> expr = context.resolve(expression, QueryDslExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve expression [" + expression + "]"));
		expr.validate();
		return expr.getExpression();
	}

	private static Predicate[] resolveFilterList(List<QueryFilter> filters, QueryDslResolutionContext context)
			throws InvalidExpressionException {
		List<Predicate> resolved = new LinkedList<>();
		filters.forEach(f -> {
			PredicateExpression prd = context.resolve(f, PredicateExpression.class, context)
					.orElseThrow(() -> new InvalidExpressionException("Failed to resolve QueryFilter [" + f + "]"));
			prd.validate();
			resolved.add(prd.getPredicate());
		});
		return resolved.toArray(new Predicate[resolved.size()]);
	}

}
