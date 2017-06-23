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
package com.holonplatform.datastore.jpa.internal.querydsl.expressions;

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.querydsl.core.QueryMetadata;

/**
 * Default {@link QueryDslResolutionContext} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultQueryDslResolutionContext implements QueryDslResolutionContext {

	private final QueryDslResolutionContext parent;
	private final ExpressionResolverHandler expressionResolverHandler;
	private final QueryMetadata queryMetadata;

	public DefaultQueryDslResolutionContext(QueryDslResolutionContext parent,
			ExpressionResolverHandler expressionResolverHandler, QueryMetadata queryMetadata) {
		super();

		ObjectUtils.argumentNotNull(expressionResolverHandler, "ExpressionResolverHandler must be not null");

		this.parent = parent;
		this.expressionResolverHandler = expressionResolverHandler;
		this.queryMetadata = queryMetadata;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler#resolve(com.holonplatform.core.Expression,
	 * java.lang.Class, com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	public <E extends Expression, R extends Expression> Optional<R> resolve(E expression, Class<R> resolutionType,
			ResolutionContext context) throws InvalidExpressionException {
		return expressionResolverHandler.resolve(expression, resolutionType, context);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslResolutionContext#getParent()
	 */
	@Override
	public Optional<QueryDslResolutionContext> getParent() {
		return Optional.ofNullable(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslResolutionContext#getQueryMetadata()
	 */
	@Override
	public Optional<QueryMetadata> getQueryMetadata() {
		return Optional.ofNullable(queryMetadata);
	}

}
