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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslAggregation;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.Expression;

/**
 * QueryDsl {@link QueryAggregation} resolver.
 *
 * @since 5.0.0
 */
@Priority(Integer.MAX_VALUE)
public enum QueryDslAggregationResolver
		implements QueryDslContextExpressionResolver<QueryAggregation, QueryDslAggregation> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryAggregation> getExpressionType() {
		return QueryAggregation.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends QueryDslAggregation> getResolvedType() {
		return QueryDslAggregation.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@Override
	public Optional<QueryDslAggregation> resolve(QueryAggregation expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// group by
		final List<Expression<?>> groupBys = new ArrayList<>(expression.getAggregationPaths().length);
		for (Path<?> path : expression.getAggregationPaths()) {
			groupBys.add(context.resolveOrFail(path, QueryDslExpression.class).getExpression());
		}

		// having
		QueryDslAggregation aggregation = expression.getAggregationFilter()
				.map(f -> QueryDslAggregation.create(groupBys,
						context.resolveOrFail(f, PredicateExpression.class).getPredicate()))
				.orElse(QueryDslAggregation.create(groupBys, null));

		return Optional.of(aggregation);
	}
}
