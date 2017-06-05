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

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.querydsl.core.QueryMetadata;

/**
 * QueryDsl {@link ResolutionContext}.
 *
 * @since 5.0.0
 */
public interface QueryDslResolutionContext extends ResolutionContext {

	/**
	 * Get the optional parent context.
	 * @return the optional parent context
	 */
	Optional<QueryDslResolutionContext> getParent();

	/**
	 * Get the query metadata.
	 * @return the query metadata
	 */
	Optional<QueryMetadata> getQueryMetadata();

	/**
	 * Create a new {@link QueryDslResolutionContext}.
	 * @param parent Optional parent context
	 * @param expressionResolverHandler Expression resolver handler (not null)
	 * @param queryMetadata Query metadata (not null)
	 * @return A new {@link QueryDslResolutionContext}
	 */
	static QueryDslResolutionContext create(QueryDslResolutionContext parent,
			ExpressionResolverHandler expressionResolverHandler, QueryMetadata queryMetadata) {
		return new DefaultQueryDslResolutionContext(parent, expressionResolverHandler, queryMetadata);
	}

	/**
	 * Check the given context is a {@link QueryDslResolutionContext}.
	 * @param context Context to check (not null)
	 * @return The QueryDslResolutionContext
	 * @throws InvalidExpressionException If given context is not a QueryDslResolutionContext
	 */
	static QueryDslResolutionContext checkContext(ResolutionContext context) {
		ObjectUtils.argumentNotNull(context, "Null ResolutionContext");
		if (!QueryDslResolutionContext.class.isAssignableFrom(context.getClass())) {
			throw new InvalidExpressionException("Invalid ResolutionContext type: expected ["
					+ QueryDslResolutionContext.class.getName() + "], got [" + context.getClass().getName() + "]");
		}
		return (QueryDslResolutionContext) context;
	}

}
