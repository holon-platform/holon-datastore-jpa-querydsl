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
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection;

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.query.CountAllProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.DefaultQueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.dsl.Wildcard;

/**
 * {@link CountAllProjection} resolver.
 *
 * @since 5.1.0
 */
@Priority(Integer.MAX_VALUE - 100)
public enum QueryDslCountAllProjectionResolver
		implements QueryDslContextExpressionResolver<CountAllProjection, QueryDslProjection> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends CountAllProjection> getExpressionType() {
		return CountAllProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends QueryDslProjection> getResolvedType() {
		return QueryDslProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@Override
	public Optional<QueryDslProjection> resolve(CountAllProjection expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {
		// validate
		expression.validate();

		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(Wildcard.count);
		return Optional.of(p);
	}

}
