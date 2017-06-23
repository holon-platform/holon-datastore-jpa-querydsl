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

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;

/**
 * Default {@link OrderSpecifierExpression} implementation.
 *
 * @since 5.0.0
 */
public class DefaultOrderSpecifierExpression implements OrderSpecifierExpression {

	private static final long serialVersionUID = 4589999881057307044L;

	private final List<OrderSpecifier<?>> orderSpecifiers;

	public DefaultOrderSpecifierExpression(List<OrderSpecifier<?>> orderSpecifiers) {
		super();
		this.orderSpecifiers = orderSpecifiers;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.OrderSpecifierExpression#getOrderSpecifiers()
	 */
	@Override
	public List<OrderSpecifier<?>> getOrderSpecifiers() {
		return orderSpecifiers;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getOrderSpecifiers() == null || getOrderSpecifiers().isEmpty()) {
			throw new InvalidExpressionException("No OrderSpecifier available");
		}
	}

}
