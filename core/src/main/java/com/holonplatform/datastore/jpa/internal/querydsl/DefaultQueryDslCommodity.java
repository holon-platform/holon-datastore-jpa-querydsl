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
package com.holonplatform.datastore.jpa.internal.querydsl;

import java.util.function.Supplier;

import javax.persistence.EntityManager;

import com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.jpa.querydsl.JpaQuery;
import com.holonplatform.datastore.jpa.querydsl.QueryDsl;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAUpdateClause;

/**
 * Default {@link QueryDsl} commodity implementation.
 *
 * @since 5.0.0
 */
public class DefaultQueryDslCommodity implements QueryDsl {

	private static final long serialVersionUID = 4994505016202424457L;

	private final Supplier<EntityManager> entityManagerProvider;
	private final ExpressionResolverSupport expressionResolversProvider;

	public DefaultQueryDslCommodity(Supplier<EntityManager> entityManagerProvider,
			ExpressionResolverSupport expressionResolversProvider) {
		super();
		ObjectUtils.argumentNotNull(entityManagerProvider, "EntityManager supplier must be not null");
		this.entityManagerProvider = entityManagerProvider;
		this.expressionResolversProvider = expressionResolversProvider;
	}

	private EntityManager getEntityManager() {
		EntityManager em = entityManagerProvider.get();
		if (em == null) {
			throw new IllegalStateException("EntityManager not available");
		}
		return em;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.QueryFactory#query()
	 */
	@Override
	public JpaQuery<?> query() {
		return new JpaQuery<>(expressionResolversProvider, getEntityManager());
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.QueryDsl#update(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public UpdateClause<?> update(EntityPath<?> path) {
		return new JPAUpdateClause(getEntityManager(), path);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.QueryDsl#delete(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public DeleteClause<?> delete(EntityPath<?> path) {
		return new JPADeleteClause(getEntityManager(), path);
	}

}
