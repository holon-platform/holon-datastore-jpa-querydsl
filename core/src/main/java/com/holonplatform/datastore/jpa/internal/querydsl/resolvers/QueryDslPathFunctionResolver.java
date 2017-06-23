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

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.query.FunctionExpression.PathFunctionExpression;
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.QueryFunction.Avg;
import com.holonplatform.core.query.QueryFunction.Count;
import com.holonplatform.core.query.QueryFunction.Max;
import com.holonplatform.core.query.QueryFunction.Min;
import com.holonplatform.core.query.QueryFunction.Sum;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;

/**
 * QueryDsl JPA {@link PathFunctionExpression} expression resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryDslPathFunctionResolver implements ExpressionResolver<PathFunctionExpression, QueryDslExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends PathFunctionExpression> getExpressionType() {
		return PathFunctionExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends QueryDslExpression> getResolvedType() {
		return QueryDslExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<QueryDslExpression> resolve(PathFunctionExpression expression, ResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		// resolve path
		QueryDslExpression path = context.resolve(expression.getPath(), QueryDslExpression.class, context).orElseThrow(
				() -> new InvalidExpressionException("Failed to resolve function path [" + expression.getPath() + "]"));
		path.validate();

		// resolve function
		final QueryFunction<?> function = expression.getFunction();

		Expression<?> functionExpression = null;

		if (Count.class.isAssignableFrom(function.getClass())) {
			functionExpression = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_AGG, path.getExpression());
		} else if (Avg.class.isAssignableFrom(function.getClass())) {
			functionExpression = Expressions.numberOperation(Double.class, Ops.AggOps.AVG_AGG, path.getExpression());
		} else if (Min.class.isAssignableFrom(function.getClass())) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.MIN_AGG,
					path.getExpression());
		} else if (Max.class.isAssignableFrom(function.getClass())) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.MAX_AGG,
					path.getExpression());
		} else if (Sum.class.isAssignableFrom(function.getClass())) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.SUM_AGG,
					path.getExpression());
		}

		if (functionExpression != null) {
			return Optional.of(QueryDslExpression.create(functionExpression));
		}

		return Optional.empty();
	}

}
