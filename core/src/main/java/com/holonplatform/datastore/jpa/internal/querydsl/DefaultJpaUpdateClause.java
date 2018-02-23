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
package com.holonplatform.datastore.jpa.internal.querydsl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.jpa.context.JpaOperationContext;
import com.holonplatform.datastore.jpa.querydsl.JpaUpdateClause;
import com.querydsl.core.JoinType;
import com.querydsl.core.support.QueryMixin;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAUtil;

/**
 * Default {@link JpaUpdateClause} implementation.
 *
 * @since 5.1.0
 */
public class DefaultJpaUpdateClause implements JpaUpdateClause {

	protected final JpaOperationContext operationContext;

	private final QueryMixin<?> queryMixin = new JPAQueryMixin<Void>();

	private final Map<Path<?>, Expression<?>> updates = new LinkedHashMap<>();

	public DefaultJpaUpdateClause(JpaOperationContext operationContext, EntityPath<?> entity) {
		super();
		ObjectUtils.argumentNotNull(entity, "Entity to update must be not null");
		this.operationContext = operationContext;
		this.queryMixin.addJoin(JoinType.DEFAULT, entity);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.UpdateClause#set(java.util.List, java.util.List)
	 */
	@Override
	public JpaUpdateClause set(List<? extends Path<?>> paths, List<?> values) {
		for (int i = 0; i < paths.size(); i++) {
			if (values.get(i) != null) {
				updates.put(paths.get(i), Expressions.constant(values.get(i)));
			} else {
				updates.put(paths.get(i), Expressions.nullExpression(paths.get(i)));
			}
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.StoreClause#set(com.querydsl.core.types.Path, java.lang.Object)
	 */
	@Override
	public <T> JpaUpdateClause set(Path<T> path, T value) {
		if (value != null) {
			updates.put(path, Expressions.constant(value));
		} else {
			setNull(path);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.StoreClause#set(com.querydsl.core.types.Path, com.querydsl.core.types.Expression)
	 */
	@Override
	public <T> JpaUpdateClause set(Path<T> path, Expression<? extends T> expression) {
		if (expression != null) {
			updates.put(path, expression);
		} else {
			setNull(path);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.StoreClause#setNull(com.querydsl.core.types.Path)
	 */
	@Override
	public <T> JpaUpdateClause setNull(Path<T> path) {
		updates.put(path, Expressions.nullExpression(path));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.FilteredClause#where(com.querydsl.core.types.Predicate[])
	 */
	@Override
	public JpaUpdateClause where(Predicate... o) {
		for (Predicate p : o) {
			queryMixin.where(p);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.StoreClause#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return updates.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.dml.DMLClause#execute()
	 */
	@Override
	public long execute() {
		return operationContext.withEntityManager(entityManager -> {

			JPQLSerializer serializer = new JPQLSerializer(JPAProvider.getTemplates(entityManager), entityManager);
			serializer.serializeForUpdate(queryMixin.getMetadata(), updates);
			Map<Object, String> constants = serializer.getConstantToLabel();

			// trace
			final String queryString = serializer.toString();
			operationContext.trace(queryString.replace('\n', ' '));

			Query query = entityManager.createQuery(queryString);

			JPAUtil.setConstants(query, constants, queryMixin.getMetadata().getParams());
			return Long.valueOf(query.executeUpdate());

		});
	}

}
