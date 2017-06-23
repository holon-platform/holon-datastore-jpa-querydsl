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
package com.holonplatform.datastore.jpa.querydsl;

import java.util.List;

import javax.persistence.EntityManager;

import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverBuilder;
import com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport;
import com.holonplatform.core.internal.query.DefaultQueryDefinition;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.query.Query;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.core.query.QueryAggregation.QueryAggregationSupport;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QueryFilter.QueryFilterSupport;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.core.query.QuerySort.QuerySortSupport;
import com.holonplatform.datastore.jpa.internal.querydsl.QueryDSLUtils;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslAggregationResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslConstantExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslExistFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslNotExistFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPathFunctionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPathResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPropertyConstantExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPropertyResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslQueryFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslQuerySortResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslSubqueryResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslTargetEntityPathResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslVisitableQueryFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslVisitableQuerySortResolver;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.AbstractJPAQuery;
import com.querydsl.jpa.impl.JPAProvider;

/**
 * A QueryDSL (www.querydsl.com) {@link JPQLQuery} extension that supports {@link Query} clauses and
 * {@link ExpressionResolver}s.
 * 
 * <p>
 * Furthermore, some QueryDSL 3.x backward compatibility methods are provided, such as {@link #list(Expression)} and
 * {@link #uniqueResult(Expression)}.
 * </p>
 * 
 * @param <T> Query result type
 * 
 * @since 5.0.0
 */
public class JpaQuery<T> extends AbstractJPAQuery<T, JpaQuery<T>> implements QueryFilterSupport<JpaQuery<T>>,
		QuerySortSupport<JpaQuery<T>>, QueryAggregationSupport<JpaQuery<T>>, ExpressionResolverBuilder<JpaQuery<T>> {

	private static final long serialVersionUID = 5196596843500361538L;

	/**
	 * Optional expresion resolvers provider
	 */
	private final ExpressionResolverSupport expressionResolversProvider;

	/**
	 * Query definition
	 */
	private final QueryDefinition queryDefinition;

	/**
	 * Constructor
	 * @param expressionResolversProvider Optional {@link ExpressionResolverSupport} to inherit expression resolvers
	 *        from
	 * @param entityManager EntityManager
	 */
	public JpaQuery(ExpressionResolverSupport expressionResolversProvider, EntityManager entityManager) {
		this(expressionResolversProvider, entityManager, JPAProvider.getTemplates(entityManager),
				new DefaultQueryMetadata());
	}

	@SuppressWarnings("unchecked")
	protected JpaQuery(ExpressionResolverSupport expressionResolversProvider, EntityManager entityManager,
			JPQLTemplates templates, QueryMetadata metadata) {
		super(entityManager, templates, metadata);
		this.expressionResolversProvider = expressionResolversProvider;
		this.queryDefinition = new DefaultQueryDefinition();

		// default resolvers
		this.queryDefinition.addExpressionResolver(QueryDslTargetEntityPathResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPropertyResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPathResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPathFunctionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslConstantExpressionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPropertyConstantExpressionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslSubqueryResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslQueryFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslQuerySortResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslExistFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslNotExistFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslVisitableQueryFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslVisitableQuerySortResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslAggregationResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslProjectionResolver.INSTANCE);

		if (expressionResolversProvider != null) {
			// inherit resolvers
			expressionResolversProvider.getExpressionResolvers().forEach(r -> queryDefinition.addExpressionResolver(r));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JpaQuery<T> clone(EntityManager entityManager, JPQLTemplates templates) {
		JpaQuery<T> q = new JpaQuery<>(expressionResolversProvider, entityManager, templates, getMetadata().clone());
		q.clone(this);
		return q;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JpaQuery<T> clone(EntityManager entityManager) {
		return clone(entityManager, JPAProvider.getTemplates(entityManager));
	}

	/**
	 * Change the projection of this query
	 * @param <U> Results type
	 * @param expr new projection
	 * @return the current object
	 */
	@Override
	public <U> JpaQuery<U> select(Expression<U> expr) {
		queryMixin.setProjection(expr);
		@SuppressWarnings("unchecked") // This is the new type
		JpaQuery<U> newType = (JpaQuery<U>) this;
		return newType;
	}

	/**
	 * Change the projection of this query
	 * @param exprs new projection
	 * @return the current object
	 */
	@Override
	public JpaQuery<Tuple> select(Expression<?>... exprs) {
		queryMixin.setProjection(exprs);
		@SuppressWarnings("unchecked") // This is the new type
		JpaQuery<Tuple> newType = (JpaQuery<Tuple>) this;
		return newType;
	}

	/**
	 * Query definition
	 * @return the queryDefinition
	 */
	protected QueryDefinition getQueryDefinition() {
		return queryDefinition;
	}

	/**
	 * Shorter for count(*) on selected entity path
	 * @return Count result
	 */
	public long count() {
		return select(Wildcard.count).fetchCount();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverBuilder#withExpressionResolver(com.holonplatform.core
	 * .ExpressionResolver)
	 */
	@Override
	public <E extends com.holonplatform.core.Expression, R extends com.holonplatform.core.Expression> JpaQuery<T> withExpressionResolver(
			ExpressionResolver<E, R> expressionResolver) {
		getQueryDefinition().addExpressionResolver(expressionResolver);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QuerySort.QuerySortSupport#sort(com.holonplatform.core.query.QuerySort)
	 */
	@Override
	public JpaQuery<T> sort(QuerySort sort) {
		if (sort != null) {
			getQueryDefinition().addSort(sort);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryFilterClause#filter(com.holonplatform.core.query.QueryFilter)
	 */
	@Override
	public JpaQuery<T> filter(QueryFilter filter) {
		if (filter != null) {
			getQueryDefinition().addFilter(filter);
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.query.QueryAggregation.QueryAggregationSupport#aggregate(com.holonplatform.core.query.
	 * QueryAggregation)
	 */
	@Override
	public JpaQuery<T> aggregate(QueryAggregation aggregation) {
		getQueryDefinition().setAggregation(aggregation);
		return this;
	}

	@Override
	protected JPQLSerializer serialize(boolean forCountRow) {

		// query target
		if (getMetadata().getJoins() != null && !getMetadata().getJoins().isEmpty()) {
			JoinExpression join = getMetadata().getJoins().get(0);
			Expression<?> je = join.getTarget();
			if (je != null && EntityPath.class.isAssignableFrom(je.getClass())) {
				getQueryDefinition().setTarget(QueryDslTarget.of((EntityPath<?>) je));
			}
		}

		QueryDSLUtils.configureQueryFromDefinition(this, getQueryDefinition(), null);

		return super.serialize(forCountRow);
	}

	/**
	 * Convenience method for QueryDSL 3.x backward compatibility: execute query and returns results list. An empty list
	 * is returned when no result is found.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return a List over the projection
	 */
	public <RT> List<RT> list(Expression<RT> projection) {
		return select(projection).fetch();
	}

	/**
	 * Convenience method for QueryDSL 3.x backward compatibility: return a single result for the given projection or
	 * <code>null</code> if no result is found. For multiple results only the first one is returned.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return Single (first) result
	 */
	public <RT> RT singleResult(Expression<RT> projection) {
		return select(projection).fetchFirst();
	}

	/**
	 * Convenience method for QueryDSL 3.x backward compatibility: return a unique result for the given projection or
	 * <code>null</code> if no result is found.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return Unique result
	 * @throws NonUniqueResultException if there is more than one matching result
	 */
	public <RT> RT uniqueResult(Expression<RT> projection) {
		return select(projection).fetchOne();
	}

}
