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
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.internal.query.QuerySortVisitor;
import com.holonplatform.core.internal.query.QuerySortVisitor.VisitableQuerySort;
import com.holonplatform.core.internal.query.QueryUtils;
import com.holonplatform.core.query.QuerySort.CompositeQuerySort;
import com.holonplatform.core.query.QuerySort.PathQuerySort;
import com.holonplatform.core.query.QuerySort.SortDirection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.OrderSpecifierExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

/**
 * QueryDsl {@link VisitableQuerySort} expression resolver.
 * 
 * @since 5.0.0
 */
@Priority(Integer.MAX_VALUE - 12)
public enum QueryDslVisitableQuerySortResolver
		implements QueryDslContextExpressionResolver<VisitableQuerySort, OrderSpecifierExpression>,
		QuerySortVisitor<OrderSpecifierExpression, QueryDslResolutionContext> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends VisitableQuerySort> getExpressionType() {
		return VisitableQuerySort.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends OrderSpecifierExpression> getResolvedType() {
		return OrderSpecifierExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@Override
	public Optional<OrderSpecifierExpression> resolve(VisitableQuerySort expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve using visitor
		return Optional.ofNullable(expression.accept(this, context));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QuerySortVisitor#visit(com.holonplatform.core.query.QuerySort.
	 * PathQuerySort, java.lang.Object)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public OrderSpecifierExpression visit(PathQuerySort<?> sort, QueryDslResolutionContext context) {
		QueryDslExpression<?> path = context.resolveOrFail(sort.getPath(), QueryDslExpression.class);
		return OrderSpecifierExpression.create(new OrderSpecifier(
				(sort.getDirection() == SortDirection.DESCENDING) ? Order.DESC : Order.ASC, path.getExpression()));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QuerySortVisitor#visit(com.holonplatform.core.query.QuerySort.
	 * CompositeQuerySort, java.lang.Object)
	 */
	@Override
	public OrderSpecifierExpression visit(CompositeQuerySort sort, QueryDslResolutionContext context) {
		List<OrderSpecifier<?>> resolved = new LinkedList<>();
		QueryUtils.flattenQuerySort(sort).forEach(s -> {
			OrderSpecifierExpression ose = context.resolveOrFail(s, OrderSpecifierExpression.class);
			resolved.addAll(ose.getOrderSpecifiers());
		});
		return OrderSpecifierExpression.create(resolved);
	}

}
