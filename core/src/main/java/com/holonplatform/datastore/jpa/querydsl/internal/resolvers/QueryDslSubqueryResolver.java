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

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.relational.SubQuery;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.datastore.jpa.querydsl.internal.DefaultJpaQuery;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.EntityPathExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslResolutionContext;
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
public enum QueryDslSubqueryResolver implements QueryDslContextExpressionResolver<SubQuery, QueryDslExpression> {

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
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@Override
	public Optional<QueryDslExpression> resolve(SubQuery expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		expression.validate();

		try {

			final QueryConfiguration configuration = expression.getQueryConfiguration();

			// target
			final QueryDslResolutionContext targetContext = context.childContext();
			// resolve entity path
			EntityPathExpression<?> expr = targetContext
					.resolve(
							configuration.getTarget()
									.orElseThrow(() -> new InvalidExpressionException("Missing sub query target")),
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
			DefaultJpaQuery.configureQuery(query, configuration, targetContext);

			return Optional.of(QueryDslExpression.create(query));

		} catch (Exception e) {
			throw new InvalidExpressionException("Failed to resolve sub query [" + expression + "]", e);
		}
	}
}
