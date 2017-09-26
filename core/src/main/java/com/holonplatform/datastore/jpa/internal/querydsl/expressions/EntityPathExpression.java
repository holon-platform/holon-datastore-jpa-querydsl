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
package com.holonplatform.datastore.jpa.internal.querydsl.expressions;

import java.io.Serializable;

import com.holonplatform.core.Expression;
import com.querydsl.core.types.EntityPath;

/**
 * QueryDsl {@link EntityPath} expression.
 * 
 * @param <T> Entity path type
 *
 * @since 5.0.0
 */
public interface EntityPathExpression<T> extends Expression, Serializable {

	/**
	 * Get the entity path.
	 * @return the entity path
	 */
	EntityPath<T> getEntityPath();

	/**
	 * Create a new {@link EntityPathExpression} using given <code>entityPath</code>.
	 * @param <T> Entity path type
	 * @param entityPath Entity path
	 * @return A new {@link EntityPathExpression}
	 */
	static <T> EntityPathExpression<T> create(EntityPath<T> entityPath) {
		return new DefaultEntityPathExpression<>(entityPath);
	}

}
