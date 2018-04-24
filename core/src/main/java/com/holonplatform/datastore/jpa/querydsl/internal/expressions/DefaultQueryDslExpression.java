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

import com.querydsl.core.types.Expression;

/**
 * Default {@link QueryDslExpression} implementation.
 *
 * @param <T> Expression type
 * 
 * @since 5.0.0
 */
public class DefaultQueryDslExpression<T> implements QueryDslExpression<T> {

	private static final long serialVersionUID = -2380692048679613713L;

	private final Expression<T> expression;

	public DefaultQueryDslExpression(Expression<T> expression) {
		super();
		this.expression = expression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslExpression#getExpression()
	 */
	@Override
	public Expression<T> getExpression() {
		return expression;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getExpression() == null) {
			throw new InvalidExpressionException("Null expression");
		}
	}

}
