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

import com.querydsl.core.types.EntityPath;

/**
 * Default {@link EntityPathExpression} implementation.
 *
 * @param <T> Entity path type
 *
 * @since 5.0.0
 */
public class DefaultEntityPathExpression<T> implements EntityPathExpression<T> {

	private static final long serialVersionUID = -6719976097958162203L;

	private final EntityPath<T> entityPath;

	public DefaultEntityPathExpression(EntityPath<T> entityPath) {
		super();
		this.entityPath = entityPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.EntityPathExpression#getEntityPath()
	 */
	@Override
	public EntityPath<T> getEntityPath() {
		return entityPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getEntityPath() == null) {
			throw new InvalidExpressionException("Null entity path");
		}
	}
}
