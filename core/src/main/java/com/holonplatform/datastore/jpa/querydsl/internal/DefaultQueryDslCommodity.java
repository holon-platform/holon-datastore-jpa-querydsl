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

import com.holonplatform.datastore.jpa.context.JpaOperationContext;
import com.holonplatform.datastore.jpa.querydsl.JpaDeleteClause;
import com.holonplatform.datastore.jpa.querydsl.JpaUpdateClause;
import com.holonplatform.datastore.jpa.querydsl.QueryDsl;
import com.querydsl.core.types.EntityPath;

/**
 * Default {@link QueryDsl} commodity implementation.
 *
 * @since 5.0.0
 */
public class DefaultQueryDslCommodity implements QueryDsl {

	private static final long serialVersionUID = 4994505016202424457L;

	private final JpaOperationContext operationContext;

	public DefaultQueryDslCommodity(JpaOperationContext operationContext) {
		super();
		this.operationContext = operationContext;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.QueryFactory#query()
	 */
	@Override
	public DefaultJpaQuery<?> query() {
		return new DefaultJpaQuery<>(operationContext);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.QueryDsl#update(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public JpaUpdateClause update(EntityPath<?> entity) {
		return new DefaultJpaUpdateClause(operationContext, entity);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.QueryDsl#delete(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public JpaDeleteClause delete(EntityPath<?> entity) {
		return new DefaultJpaDeleteClause(operationContext, entity);
	}

}
