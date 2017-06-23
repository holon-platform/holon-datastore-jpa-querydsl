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

import com.querydsl.core.types.Predicate;

/**
 * Default {@link PredicateExpression} implementation.
 *
 * @since 5.0.0
 */
public class DefaultPredicateExpression implements PredicateExpression {

	private static final long serialVersionUID = -679860437656770686L;

	private final Predicate predicate;

	public DefaultPredicateExpression(Predicate predicate) {
		super();
		this.predicate = predicate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.PredicateExpression#getPredicate()
	 */
	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getPredicate() == null) {
			throw new InvalidExpressionException("Null predicate");
		}
	}

}
