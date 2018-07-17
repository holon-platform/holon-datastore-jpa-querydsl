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
package com.holonplatform.datastore.jpa.querydsl.internal;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.jpa.querydsl.QueryDslTarget;
import com.querydsl.core.types.EntityPath;

/**
 * {@link DataTarget} using a QueryDSL {@link EntityPath}
 * 
 * @since 4.4.0
 */
public class DefaultQueryDslTarget<T> implements QueryDslTarget<T> {

	private static final long serialVersionUID = -6539734576046688595L;

	private final EntityPath<T> entityPath;

	/**
	 * Constructor
	 * @param entityPath EntityPath
	 */
	public DefaultQueryDslTarget(EntityPath<T> entityPath) {
		super();
		ObjectUtils.argumentNotNull(entityPath, "EntityPath must be not null");
		this.entityPath = entityPath;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryExpression#getType()
	 */
	@Override
	public Class<? extends T> getType() {
		return entityPath.getType();
	}

	@Override
	public EntityPath<T> getEntityPath() {
		return entityPath;
	}

	@Override
	public String getName() {
		return entityPath.getMetadata().getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (entityPath == null) {
			throw new InvalidExpressionException("Null entity path");
		}
	}

}
