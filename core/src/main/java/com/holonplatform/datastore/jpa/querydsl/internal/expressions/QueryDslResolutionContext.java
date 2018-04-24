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
package com.holonplatform.datastore.jpa.querydsl.internal.expressions;

import java.util.Optional;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.datastore.jpa.context.JpaContext;
import com.querydsl.core.QueryMetadata;

/**
 * QueryDsl {@link ResolutionContext}.
 *
 * @since 5.0.0
 */
public interface QueryDslResolutionContext extends JpaContext, ResolutionContext, ExpressionResolverSupport {

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
	 * Try to resolve given <code>expression</code> using current context resolvers to obtain a
	 * <code>resolutionType</code> type expression.
	 * <p>
	 * The resolved expression is validate using {@link Expression#validate()} before returning it to caller.
	 * </p>
	 * @param <E> Expression type
	 * @param <R> Resolution type
	 * @param expression Expression to resolve
	 * @param resolutionType Expression type to obtain
	 * @return Resolved expression
	 */
	default <E extends Expression, R extends Expression> Optional<R> resolve(E expression, Class<R> resolutionType)
			throws InvalidExpressionException {
		// resolve
		return resolve(expression, resolutionType, this).map(e -> {
			// validate
			e.validate();
			return e;
		});
	}

	/**
	 * Resolve given <code>expression</code> using current context resolvers to obtain a <code>resolutionType</code>
	 * type expression. If no {@link ExpressionResolver} is available to resolve given expression, an
	 * {@link InvalidExpressionException} is thrown.
	 * <p>
	 * The resolved expression is validate using {@link Expression#validate()} before returning it to caller.
	 * </p>
	 * @param <E> Expression type
	 * @param <R> Resolution type
	 * @param expression Expression to resolve
	 * @param resolutionType Expression type to obtain
	 * @return Resolved expression
	 * @throws InvalidExpressionException If an error occurred during resolution, or if no {@link ExpressionResolver} is
	 *         available to resolve given expression or if expression validation failed
	 */
	default <E extends Expression, R extends Expression> R resolveOrFail(E expression, Class<R> resolutionType) {
		return resolve(expression, resolutionType)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve expression [" + expression + "]"));
	}

	// builders

	/**
	 * Create a new {@link QueryDslResolutionContext} as child of this context. This context will be setted as parent of
	 * the new context.
	 * @return A new {@link QueryDslResolutionContext} with this context as parent
	 */
	QueryDslResolutionContext childContext();

	/**
	 * Create a new default {@link QueryDslResolutionContext}.
	 * @param context JPA context to use (not null)
	 * @param queryMetadata Query metadata
	 * @return A new {@link QueryDslResolutionContext}
	 */
	static QueryDslResolutionContext create(JpaContext context, QueryMetadata queryMetadata) {
		return new DefaultQueryDslResolutionContext(context, queryMetadata);
	}

}
