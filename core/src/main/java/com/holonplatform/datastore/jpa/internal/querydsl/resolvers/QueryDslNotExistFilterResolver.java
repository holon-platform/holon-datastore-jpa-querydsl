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
import com.holonplatform.core.internal.datastore.relational.NotExistsFilter;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.querydsl.core.support.ExtendedSubQuery;
import com.querydsl.core.types.Expression;

/**
 * QueryDsl {@link NotExistsFilter} expression resolver.
 *
 * @since 5.0.0
 *
 */
@Priority(Integer.MAX_VALUE - 50)
public enum QueryDslNotExistFilterResolver implements ExpressionResolver<NotExistsFilter, PredicateExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends NotExistsFilter> getExpressionType() {
		return NotExistsFilter.class;
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
	public Optional<PredicateExpression> resolve(NotExistsFilter expression, ResolutionContext context)
			throws InvalidExpressionException {
		// validate
		expression.validate();

		// resolve subquery
		QueryDslExpression<?> subquery = context.resolve(expression.getSubQuery(), QueryDslExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException(
						"Failed to resolve expression [" + expression.getSubQuery() + "]"));
		subquery.validate();

		final Expression<?> subQueryExpression = subquery.getExpression();
		if (!ExtendedSubQuery.class.isAssignableFrom(subQueryExpression.getClass())) {
			throw new InvalidExpressionException(
					"The resolved ExistsFilter subquery expression is not a subquery expression: [" + subQueryExpression
							+ "]");
		}

		return Optional.of(PredicateExpression.create(((ExtendedSubQuery<?>) subQueryExpression).notExists()));
	}
}
