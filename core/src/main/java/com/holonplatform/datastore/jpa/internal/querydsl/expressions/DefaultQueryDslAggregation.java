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
import java.util.Optional;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

/**
 * Default {@link QueryDslAggregation} implementation.
 *
 * @since 5.0.0
 */
public class DefaultQueryDslAggregation implements QueryDslAggregation {

	private static final long serialVersionUID = -9212265882180859774L;

	private final List<Expression<?>> groupBys;
	private final Predicate having;

	public DefaultQueryDslAggregation(List<Expression<?>> groupBys, Predicate having) {
		super();
		this.groupBys = groupBys;
		this.having = having;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getGroupBys() == null || getGroupBys().isEmpty()) {
			throw new InvalidExpressionException("No group by expression available");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslAggregation#getGroupBys()
	 */
	@Override
	public List<Expression<?>> getGroupBys() {
		return groupBys;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslAggregation#getHaving()
	 */
	@Override
	public Optional<Predicate> getHaving() {
		return Optional.ofNullable(having);
	}

}
