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
import com.holonplatform.core.datastore.relational.SubQuery;
import com.holonplatform.core.query.Query.QueryBuildException;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.datastore.jpa.internal.querydsl.QueryDSLUtils;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.EntityPathExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

/**
 * QueryDsl subquery resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 100)
public enum QueryDslSubqueryResolver implements ExpressionResolver<SubQuery, QueryDslExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends SubQuery> getExpressionType() {
		return SubQuery.class;
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
	@Override
	public Optional<QueryDslExpression> resolve(SubQuery expression, ResolutionContext resolutionContext)
			throws InvalidExpressionException {

		expression.validate();

		try {

			final QueryDslResolutionContext parentContext = QueryDslResolutionContext.checkContext(resolutionContext);

			final QueryConfiguration configuration = expression.getQueryConfiguration();

			// target
			final QueryDslResolutionContext targetContext = QueryDslResolutionContext.create(parentContext,
					configuration, null);
			// resolve entity path
			EntityPathExpression<?> expr = targetContext
					.resolve(
							configuration.getTarget()
									.orElseThrow(() -> new QueryBuildException("Missing sub query target")),
							EntityPathExpression.class, targetContext)
					.orElseThrow(() -> new InvalidExpressionException(
							"Failed to resolve target [" + configuration.getTarget() + "]"));
			expr.validate();

			// projection
			QueryDslProjection projection = targetContext
					.resolve(expression.getSelection(), QueryDslProjection.class, targetContext)
					.orElseThrow(() -> new InvalidExpressionException(
							"Failed to resolve projection [" + expression.getSelection() + "]"));
			projection.validate();

			// create subquery
			JPQLQuery<Tuple> query = JPAExpressions
					.select(projection.getSelection().toArray(new com.querydsl.core.types.Expression<?>[0]));

			// from
			query.from(expr.getEntityPath());

			// configure
			QueryDSLUtils.configureQueryFromDefinition(query, configuration, parentContext);

			return Optional.of(QueryDslExpression.create(query));

		} catch (Exception e) {
			throw new InvalidExpressionException("Failed to resolve sub query [" + expression + "]", e);
		}
	}
}
