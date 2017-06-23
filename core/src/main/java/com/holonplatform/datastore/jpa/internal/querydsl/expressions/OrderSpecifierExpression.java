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

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import com.holonplatform.core.Expression;
import com.querydsl.core.types.OrderSpecifier;

/**
 * QueryDsl {@link OrderSpecifier} expression.
 *
 * @since 5.0.0
 */
public interface OrderSpecifierExpression extends Expression, Serializable {

	/**
	 * Get the {@link OrderSpecifier}s.
	 * @return the {@link OrderSpecifier}s
	 */
	List<OrderSpecifier<?>> getOrderSpecifiers();

	/**
	 * Create a new {@link OrderSpecifierExpression} using given <code>orderSpecifiers</code>.
	 * @param orderSpecifiers Order specifiers
	 * @return A new {@link OrderSpecifierExpression}
	 */
	static OrderSpecifierExpression create(List<OrderSpecifier<?>> orderSpecifiers) {
		return new DefaultOrderSpecifierExpression(orderSpecifiers);
	}

	/**
	 * Create a new {@link OrderSpecifierExpression} using given <code>orderSpecifiers</code>.
	 * @param orderSpecifiers Order specifiers
	 * @return A new {@link OrderSpecifierExpression}
	 */
	static OrderSpecifierExpression create(OrderSpecifier<?>... orderSpecifiers) {
		return new DefaultOrderSpecifierExpression(Arrays.asList(orderSpecifiers));
	}

}
