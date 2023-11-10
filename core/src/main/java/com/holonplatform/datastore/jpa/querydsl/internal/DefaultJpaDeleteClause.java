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

import java.util.List;

import javax.persistence.Query;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.jpa.context.JpaOperationContext;
import com.holonplatform.datastore.jpa.querydsl.JpaDeleteClause;
import com.querydsl.core.JoinType;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAUtil;

/**
 * Default {@link JpaDeleteClause} implementation.
 *
 * @since 5.1.0
 */
public class DefaultJpaDeleteClause implements JpaDeleteClause {

	protected final JpaOperationContext operationContext;

	private final QueryMixin<?> queryMixin = new JPAQueryMixin<Void>();

	public DefaultJpaDeleteClause(JpaOperationContext operationContext, EntityPath<?> entity) {
		super();
		ObjectUtils.argumentNotNull(entity, "Entity to delete must be not null");
		this.operationContext = operationContext;
		this.queryMixin.addJoin(JoinType.DEFAULT, entity);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.FilteredClause#where(com.querydsl.core.types.Predicate[])
	 */
	@Override
	public JpaDeleteClause where(Predicate... o) {
		for (Predicate p : o) {
			queryMixin.where(p);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.DMLClause#execute()
	 */
	@Override
	public long execute() {
		return operationContext.withEntityManager(entityManager -> {

			JPQLSerializer serializer = new JPQLSerializer(JPAProvider.getTemplates(entityManager), entityManager);
			serializer.serializeForDelete(queryMixin.getMetadata());
			List<Object> constants = serializer.getConstants();

			// trace
			final String queryString = serializer.toString();
			operationContext.trace(queryString.replace('\n', ' '));

			Query query = entityManager.createQuery(queryString);

			JPAUtil.setConstants(query, constants, queryMixin.getMetadata().getParams());
			return Long.valueOf(query.executeUpdate());

		});
	}

}
