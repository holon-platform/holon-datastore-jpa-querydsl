/*
 * Copyright 2016-2017 Axioma srl.
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
package com.holonplatform.datastore.jpa.querydsl.internal.resolvers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.internal.query.QueryFilterVisitor;
import com.holonplatform.core.internal.query.QueryFilterVisitor.VisitableQueryFilter;
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
import com.holonplatform.core.query.ConstantExpression;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslResolutionContext;
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
		implements QueryDslContextExpressionResolver<VisitableQueryFilter, PredicateExpression>,
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
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@Override
	public Optional<PredicateExpression> resolve(VisitableQueryFilter expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve using visitor
		return Optional.ofNullable(expression.accept(this, context));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NullFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(NullFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression.create(Expressions.booleanOperation(Ops.IS_NULL,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotNullFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(NotNullFilter filter, QueryDslResolutionContext context) {
		return PredicateExpression.create(Expressions.booleanOperation(Ops.IS_NOT_NULL,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * EqualFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(EqualFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(Ops.EQ,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotEqualFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(NotEqualFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(Ops.NE,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * GreaterFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(GreaterFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(filter.isIncludeEquals() ? Ops.GOE : Ops.GT,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * LessFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(LessFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(filter.isIncludeEquals() ? Ops.LOE : Ops.LT,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * InFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(InFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(Ops.IN,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * NotInFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(NotInFilter<T> filter, QueryDslResolutionContext context) {
		TypedExpression<? super T> rightOperand = filter.getRightOperand()
				.orElseThrow(() -> new InvalidExpressionException("Missing right operand in filter [" + filter + "]"));
		return PredicateExpression.create(Expressions.booleanOperation(Ops.NOT_IN,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(rightOperand, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * BetweenFilter, java.lang.Object)
	 */
	@Override
	public <T> PredicateExpression visit(BetweenFilter<T> filter, QueryDslResolutionContext context) {
		ConstantExpression<T> from = ConstantExpression.create(filter.getFromValue());
		ConstantExpression<T> to = ConstantExpression.create(filter.getToValue());
		return PredicateExpression.create(Expressions.booleanOperation(Ops.BETWEEN,
				context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression(),
				context.resolveOrFail(from, QueryDslExpression.class).getExpression(),
				context.resolveOrFail(to, QueryDslExpression.class).getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryFilterVisitor#visit(com.holonplatform.core.internal.query.filter.
	 * StringMatchFilter, java.lang.Object)
	 */
	@Override
	public PredicateExpression visit(StringMatchFilter filter, QueryDslResolutionContext context) {

		// left operand
		Expression<?> left = context.resolveOrFail(filter.getLeftOperand(), QueryDslExpression.class).getExpression();

		// check value
		String value = filter.getValue();
		if (value == null) {
			throw new InvalidExpressionException("String match filter value cannot be null");
		}

		// add wildcards
		switch (filter.getMatchMode()) {
		case CONTAINS:
			value = "%" + value + "%";
			break;
		case ENDS_WITH:
			value = "%" + value;
			break;
		case STARTS_WITH:
			value = value + "%";
			break;
		default:
			break;
		}

		if (filter.isIgnoreCase()) {
			return PredicateExpression
					.create(Expressions.booleanOperation(Ops.LIKE_IC, left, ConstantImpl.create(value)));
		}

		return PredicateExpression.create(Expressions.booleanOperation(Ops.LIKE, left, ConstantImpl.create(value)));
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
