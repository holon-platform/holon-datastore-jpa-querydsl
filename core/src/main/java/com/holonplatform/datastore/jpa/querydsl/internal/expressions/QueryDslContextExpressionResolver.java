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

/**
 * An {@link ExpressionResolver} to be used with a {@link QueryDslResolutionContext} resultion context.
 * <p>
 * If the {@link ResolutionContext} is not a {@link QueryDslResolutionContext} an exception is thrown.
 * </p>
 * 
 * @param <E> Expression type
 * @param <R> Resolved expression type
 *
 * @since 5.1.0
 */
public interface QueryDslContextExpressionResolver<E extends Expression, R extends Expression>
		extends ExpressionResolver<E, R> {

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	default Optional<R> resolve(E expression, ResolutionContext context) throws InvalidExpressionException {
		if (!(context instanceof QueryDslResolutionContext)) {
			throw new InvalidExpressionException("Invalid context type: expected a ["
					+ QueryDslResolutionContext.class.getName() + ", got a [" + context.getClass().getName() + "]");
		}
		return resolve(expression, (QueryDslResolutionContext) context);
	}

	/**
	 * Resolve given <code>expression</code> into required expression type.
	 * @param expression Expression to resolve
	 * @param context Resolution context as a {@link QueryDslResolutionContext}
	 * @return The resolved expression, or an empty Optional if this resolver is not able to resolve given expression.
	 * @throws InvalidExpressionException If an expression resolution error occurred
	 */
	Optional<R> resolve(E expression, QueryDslResolutionContext context) throws InvalidExpressionException;

}
