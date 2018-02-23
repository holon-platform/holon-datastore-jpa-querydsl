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
package com.holonplatform.datastore.jpa.internal.querydsl.config;

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.datastore.relational.RelationalTarget;
import com.holonplatform.datastore.jpa.JpaTarget;
import com.holonplatform.datastore.jpa.config.JpaDatastoreExpressionResolver;
import com.holonplatform.datastore.jpa.jpql.context.JPQLResolutionContext;
import com.holonplatform.datastore.jpa.querydsl.QueryDslTarget;

/**
 * {@link QueryDslTarget} expression to {@link RelationalTarget} resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 50)
public class QueryDslRelationalTargetResolver
		implements JpaDatastoreExpressionResolver<QueryDslTarget, RelationalTarget> {

	private static final long serialVersionUID = -1906461135126688080L;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryDslTarget> getExpressionType() {
		return QueryDslTarget.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends RelationalTarget> getResolvedType() {
		return RelationalTarget.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.jpql.context.JPQLContextExpressionResolver#resolve(com.holonplatform.core.
	 * Expression, com.holonplatform.datastore.jpa.jpql.context.JPQLResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<RelationalTarget> resolve(QueryDslTarget expression, JPQLResolutionContext context)
			throws InvalidExpressionException {
		// validate
		expression.validate();

		// use target entity class
		return Optional.of(RelationalTarget.of(JpaTarget.of(expression.getEntityPath().getType())));
	}

}
