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

import java.io.Serializable;

import com.holonplatform.core.Expression;
import com.querydsl.core.types.Predicate;

/**
 * QueryDsl {@link Predicate} expression.
 *
 * @since 5.0.0
 */
public interface PredicateExpression extends Expression, Serializable {

	/**
	 * Get the QueryDsl predicate.
	 * @return the QueryDsl predicate
	 */
	Predicate getPredicate();

	/**
	 * Create a new {@link PredicateExpression} using given predicate.
	 * @param predicate Expression predicate
	 * @return A new {@link PredicateExpression}
	 */
	static PredicateExpression create(Predicate predicate) {
		return new DefaultPredicateExpression(predicate);
	}

}
