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
import java.util.List;
import java.util.Optional;

import com.holonplatform.core.Expression;
import com.querydsl.core.types.Predicate;

/**
 * QueryDsl aggregation expression.
 *
 * @since 5.0.0
 */
public interface QueryDslAggregation extends Expression, Serializable {

	/**
	 * Get the group by expressions.
	 * @return the group by expressions
	 */
	List<com.querydsl.core.types.Expression<?>> getGroupBys();

	/**
	 * Get the optional having predicate.
	 * @return the having predicate
	 */
	Optional<Predicate> getHaving();

	/**
	 * Create a new {@link QueryDslAggregation}.
	 * @param groupBys Group by expressions
	 * @param having Optional having predicate
	 * @return A new {@link QueryDslAggregation} instance
	 */
	static QueryDslAggregation create(List<com.querydsl.core.types.Expression<?>> groupBys, Predicate having) {
		return new DefaultQueryDslAggregation(groupBys, having);
	}

}
