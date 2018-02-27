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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.config.ConfigProperty;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.internal.query.DefaultQueryDefinition;
import com.holonplatform.core.internal.query.QueryDefinition;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.holonplatform.datastore.jpa.JpaDatastore;
import com.holonplatform.datastore.jpa.JpaQueryHint;
import com.holonplatform.datastore.jpa.context.JpaOperationContext;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.OrderSpecifierExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslAggregation;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslAggregationResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslCollectionExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslConstantExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslDataTargetResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslExistFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslNotExistFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPathResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslPropertyResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslQueryFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslQueryFunctionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslQuerySortResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslSubqueryResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslTargetEntityPathResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslVisitableQueryFilterResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslVisitableQuerySortResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslBeanProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslConstantExpressionProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslCountAllProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslDataTargetProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslPropertySetProjectionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection.QueryDslTypedExpressionProjectionResolver;
import com.holonplatform.datastore.jpa.querydsl.JpaQuery;
import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.DefaultQueryMetadata;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryMetadata;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.QueryResults;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.SubQueryExpressionImpl;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAQueryMixin;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLSerializer;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAProvider;
import com.querydsl.jpa.impl.JPAUtil;

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
public class DefaultJpaQuery<T> implements JpaQuery<T> {

	private static final long serialVersionUID = 5196596843500361538L;

	protected final JpaOperationContext operationContext;

	protected final QueryDefinition queryDefinition;

	protected final JPAQueryMixin<JpaQuery<T>> queryMixin;
	protected final SubQueryExpression<T> subQueryMixin;

	protected FactoryExpression<?> projection;

	@SuppressWarnings("unchecked")
	public DefaultJpaQuery(JpaOperationContext operationContext) {
		super();
		ObjectUtils.argumentNotNull(operationContext, "JpaOperationContext must be not null");

		this.operationContext = operationContext;

		this.queryDefinition = new DefaultQueryDefinition();

		this.queryMixin = new JPAQueryMixin<>(new DefaultQueryMetadata());
		this.queryMixin.setSelf(this);
		this.subQueryMixin = new SubQueryExpressionImpl<>((Class<? extends T>) Object.class, queryMixin.getMetadata());

		// default resolvers
		this.queryDefinition.addExpressionResolver(QueryDslTargetEntityPathResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslDataTargetResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPropertyResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPathResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslQueryFunctionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslConstantExpressionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslCollectionExpressionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslSubqueryResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslQueryFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslQuerySortResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslExistFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslNotExistFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslVisitableQueryFilterResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslVisitableQuerySortResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslAggregationResolver.INSTANCE);

		this.queryDefinition.addExpressionResolver(QueryDslConstantExpressionProjectionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslTypedExpressionProjectionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslDataTargetProjectionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslCountAllProjectionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslPropertySetProjectionResolver.INSTANCE);
		this.queryDefinition.addExpressionResolver(QueryDslBeanProjectionResolver.INSTANCE);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#getQueryConfiguration()
	 */
	@Override
	public QueryConfiguration getQueryConfiguration() {
		return queryDefinition;
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
		queryDefinition.addExpressionResolver(expressionResolver);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QuerySort.QuerySortSupport#sort(com.holonplatform.core.query.QuerySort)
	 */
	@Override
	public JpaQuery<T> sort(QuerySort sort) {
		ObjectUtils.argumentNotNull(sort, "QuerySort must be not null");
		queryDefinition.addSort(sort);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryFilterClause#filter(com.holonplatform.core.query.QueryFilter)
	 */
	@Override
	public JpaQuery<T> filter(QueryFilter filter) {
		ObjectUtils.argumentNotNull(filter, "QueryFilter must be not null");
		queryDefinition.addFilter(filter);
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
		queryDefinition.setAggregation(aggregation);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#parameter(java.lang.String, java.lang.Object)
	 */
	@Override
	public JpaQuery<T> parameter(String name, Object value) {
		queryDefinition.addParameter(name, value);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#parameter(com.holonplatform.core.config.ConfigProperty,
	 * java.lang.Object)
	 */
	@Override
	public <C> JpaQuery<T> parameter(ConfigProperty<C> property, C value) {
		ObjectUtils.argumentNotNull(property, "Config parameter must be not null");
		queryDefinition.addParameter(property.getKey(), value);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.datastore.DataTarget.DataTargetSupport#target(com.holonplatform.core.datastore.DataTarget)
	 */
	@Override
	public JpaQuery<T> target(DataTarget<?> target) {
		queryDefinition.setTarget(target);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#limit(int)
	 */
	@Override
	public JpaQuery<T> limit(int limit) {
		return queryMixin.limit(limit);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#offset(int)
	 */
	@Override
	public JpaQuery<T> offset(int offset) {
		return queryMixin.offset(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.query.QueryBuilder#restrict(int, int)
	 */
	@Override
	public JpaQuery<T> restrict(int limit, int offset) {
		return queryMixin.restrict(new QueryModifiers(Long.valueOf(limit), Long.valueOf(offset)));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Query#groupBy(com.querydsl.core.types.Expression[])
	 */
	@Override
	public JpaQuery<T> groupBy(Expression<?>... o) {
		return queryMixin.groupBy(o);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Query#having(com.querydsl.core.types.Predicate[])
	 */
	@Override
	public JpaQuery<T> having(Predicate... o) {
		return queryMixin.having(o);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.FetchableQuery#select(com.querydsl.core.types.Expression)
	 */
	@Override
	public <U> JpaQuery<U> select(Expression<U> expr) {
		queryMixin.setProjection(expr);
		@SuppressWarnings("unchecked") // This is the new type
		JpaQuery<U> newType = (JpaQuery<U>) this;
		return newType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#select(com.querydsl.core.types.Expression[])
	 */
	@Override
	public JpaQuery<Tuple> select(Expression<?>... exprs) {
		queryMixin.setProjection(exprs);
		@SuppressWarnings("unchecked") // This is the new type
		JpaQuery<Tuple> newType = (JpaQuery<Tuple>) this;
		return newType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#from(com.querydsl.core.types.EntityPath[])
	 */
	@Override
	public JpaQuery<T> from(EntityPath<?>... sources) {
		return queryMixin.from(sources);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#from(com.querydsl.core.types.CollectionExpression,
	 * com.querydsl.core.types.Path)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <P> JpaQuery<T> from(CollectionExpression<?, P> target, Path<P> alias) {
		return queryMixin.from(Expressions.as((Path) target, alias));
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(EntityPath<P> target) {
		return queryMixin.innerJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.EntityPath,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(EntityPath<P> target, Path<P> alias) {
		return queryMixin.innerJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.CollectionExpression)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(CollectionExpression<?, P> target) {
		return queryMixin.innerJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.CollectionExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(CollectionExpression<?, P> target, Path<P> alias) {
		return queryMixin.innerJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.MapExpression)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(MapExpression<?, P> target) {
		return queryMixin.innerJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#innerJoin(com.querydsl.core.types.MapExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> innerJoin(MapExpression<?, P> target, Path<P> alias) {
		return queryMixin.innerJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public <P> JpaQuery<T> join(EntityPath<P> target) {
		return queryMixin.join(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.EntityPath,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> join(EntityPath<P> target, Path<P> alias) {
		return queryMixin.join(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.CollectionExpression)
	 */
	@Override
	public <P> JpaQuery<T> join(CollectionExpression<?, P> target) {
		return queryMixin.join(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.CollectionExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> join(CollectionExpression<?, P> target, Path<P> alias) {
		return queryMixin.join(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.MapExpression)
	 */
	@Override
	public <P> JpaQuery<T> join(MapExpression<?, P> target) {
		return queryMixin.join(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#join(com.querydsl.core.types.MapExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> join(MapExpression<?, P> target, Path<P> alias) {
		return queryMixin.join(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(EntityPath<P> target) {
		return queryMixin.leftJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.EntityPath,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(EntityPath<P> target, Path<P> alias) {
		return queryMixin.leftJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.CollectionExpression)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(CollectionExpression<?, P> target) {
		return queryMixin.leftJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.CollectionExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(CollectionExpression<?, P> target, Path<P> alias) {
		return queryMixin.leftJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.MapExpression)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(MapExpression<?, P> target) {
		return queryMixin.leftJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#leftJoin(com.querydsl.core.types.MapExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> leftJoin(MapExpression<?, P> target, Path<P> alias) {
		return queryMixin.leftJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.EntityPath)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(EntityPath<P> target) {
		return queryMixin.rightJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.EntityPath,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(EntityPath<P> target, Path<P> alias) {
		return queryMixin.rightJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.CollectionExpression)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(CollectionExpression<?, P> target) {
		return queryMixin.rightJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.CollectionExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(CollectionExpression<?, P> target, Path<P> alias) {
		return queryMixin.rightJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.MapExpression)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(MapExpression<?, P> target) {
		return queryMixin.rightJoin(target);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#rightJoin(com.querydsl.core.types.MapExpression,
	 * com.querydsl.core.types.Path)
	 */
	@Override
	public <P> JpaQuery<T> rightJoin(MapExpression<?, P> target, Path<P> alias) {
		return queryMixin.rightJoin(target, alias);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#on(com.querydsl.core.types.Predicate[])
	 */
	@Override
	public JpaQuery<T> on(Predicate... condition) {
		return queryMixin.on(condition);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.FetchableQuery#transform(com.querydsl.core.ResultTransformer)
	 */
	@Override
	public <S> S transform(ResultTransformer<S> transformer) {
		return transformer.transform(this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.types.SubQueryExpression#getMetadata()
	 */
	@Override
	public QueryMetadata getMetadata() {
		return queryMixin.getMetadata();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#limit(long)
	 */
	@Override
	public JpaQuery<T> limit(long limit) {
		return queryMixin.limit(limit);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#offset(long)
	 */
	@Override
	public JpaQuery<T> offset(long offset) {
		return queryMixin.limit(offset);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#restrict(com.querydsl.core.QueryModifiers)
	 */
	@Override
	public JpaQuery<T> restrict(QueryModifiers modifiers) {
		return queryMixin.restrict(modifiers);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#orderBy(com.querydsl.core.types.OrderSpecifier[])
	 */
	@Override
	public JpaQuery<T> orderBy(OrderSpecifier<?>... o) {
		return queryMixin.orderBy(o);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.FilteredClause#where(com.querydsl.core.types.Predicate[])
	 */
	@Override
	public JpaQuery<T> where(Predicate... o) {
		return queryMixin.where(o);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#set(com.querydsl.core.types.ParamExpression, java.lang.Object)
	 */
	@Override
	public <P> JpaQuery<T> set(ParamExpression<P> param, P value) {
		return queryMixin.set(param, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.SimpleQuery#distinct()
	 */
	@Override
	public JpaQuery<T> distinct() {
		return queryMixin.distinct();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#eq(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression eq(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.EQ, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#eq(java.lang.Object)
	 */
	@Override
	public BooleanExpression eq(T constant) {
		return eq(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#ne(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression ne(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.NE, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#ne(java.lang.Object)
	 */
	@Override
	public BooleanExpression ne(T constant) {
		return eq(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#contains(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression contains(Expression<? extends T> right) {
		return Expressions.predicate(Ops.IN, right, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#contains(java.lang.Object)
	 */
	@Override
	public BooleanExpression contains(T constant) {
		return contains(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#exists()
	 */
	@Override
	public BooleanExpression exists() {
		QueryMetadata metadata = getMetadata();
		if (metadata.getProjection() == null) {
			queryMixin.setProjection(Expressions.ONE);
		}
		return Expressions.predicate(Ops.EXISTS, this);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#notExists()
	 */
	@Override
	public BooleanExpression notExists() {
		return exists().not();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#lt(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression lt(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.LT, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#lt(java.lang.Object)
	 */
	@Override
	public BooleanExpression lt(T constant) {
		return lt(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#gt(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression gt(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.GT, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#gt(java.lang.Object)
	 */
	@Override
	public BooleanExpression gt(T constant) {
		return gt(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#loe(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression loe(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.LOE, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#loe(java.lang.Object)
	 */
	@Override
	public BooleanExpression loe(T constant) {
		return loe(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#goe(com.querydsl.core.types.Expression)
	 */
	@Override
	public BooleanExpression goe(Expression<? extends T> expr) {
		return Expressions.predicate(Ops.GOE, this, expr);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#goe(java.lang.Object)
	 */
	@Override
	public BooleanExpression goe(T constant) {
		return goe(Expressions.constant(constant));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#isNull()
	 */
	@Override
	public BooleanOperation isNull() {
		return Expressions.booleanOperation(Ops.IS_NULL, subQueryMixin);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#isNotNull()
	 */
	@Override
	public BooleanOperation isNotNull() {
		return Expressions.booleanOperation(Ops.IS_NOT_NULL, subQueryMixin);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.types.Expression#accept(com.querydsl.core.types.Visitor, java.lang.Object)
	 */
	@Override
	public <R, C> R accept(Visitor<R, C> v, C context) {
		return subQueryMixin.accept(v, context);
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#in(java.util.Collection)
	 */
	@Override
	public BooleanExpression in(Collection<? extends T> right) {
		if (right.size() == 1) {
			return eq(right.iterator().next());
		} else {
			return Expressions.booleanOperation(Ops.IN, subQueryMixin, ConstantImpl.create(right));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.support.ExtendedSubQuery#in(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public BooleanExpression in(T... right) {
		return this.in(Arrays.asList(right));
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.types.Expression#getType()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class<? extends T> getType() {
		Expression<?> projection = queryMixin.getMetadata().getProjection();
		return (Class) (projection != null ? projection.getType() : Void.class);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#setLockMode(javax.persistence.LockModeType)
	 */
	@Override
	public JpaQuery<T> setLockMode(LockModeType lockMode) {
		queryDefinition.addParameter(JpaDatastore.QUERY_PARAMETER_LOCK_MODE.getKey(), lockMode);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#setFlushMode(javax.persistence.FlushModeType)
	 */
	@Override
	public JpaQuery<T> setFlushMode(FlushModeType flushMode) {
		queryDefinition.addParameter(JpaDatastore.QUERY_PARAMETER_FLUSH_MODE.getKey(), flushMode);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#setHint(java.lang.String, java.lang.Object)
	 */
	@Override
	public JpaQuery<T> setHint(String name, Object value) {
		queryDefinition.addParameter(JpaQueryHint.QUERY_PARAMETER_HINT.getKey(), JpaQueryHint.create(name, value));
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#fetch()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<T> fetch() {
		return operationContext.withEntityManager(entityManager -> {
			Query query = createQuery(entityManager);
			return (List<T>) getResultList(query);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#fetchJoin()
	 */
	@Override
	public JpaQuery<T> fetchJoin() {
		return queryMixin.fetchJoin();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.JpaQuery#fetchAll()
	 */
	@Override
	public JpaQuery<T> fetchAll() {
		return queryMixin.fetchAll();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#fetchFirst()
	 */
	@Override
	public T fetchFirst() {
		return limit(1).fetchOne();
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#fetchOne()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T fetchOne() {
		return operationContext.withEntityManager(entityManager -> {
			try {
				Query query = createQuery(entityManager, getMetadata().getModifiers(), false);
				return (T) getSingleResult(query);
			} catch (@SuppressWarnings("unused") javax.persistence.NoResultException e) {
				return null;
			} catch (@SuppressWarnings("unused") javax.persistence.NonUniqueResultException e) {
				throw new NonUniqueResultException();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#iterate()
	 */
	@Override
	public CloseableIterator<T> iterate() {
		return operationContext.withEntityManager(entityManager -> {
			final JPQLTemplates templates = JPAProvider.getTemplates(entityManager);
			Query query = createQuery(templates, entityManager);
			return templates.getQueryHandler().iterate(query, projection);
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#fetchResults()
	 */
	@Override
	public QueryResults<T> fetchResults() {
		return operationContext.withEntityManager(entityManager -> {
			Query countQuery = createQuery(entityManager, null, true);
			long total = (Long) countQuery.getSingleResult();
			if (total > 0) {
				QueryModifiers modifiers = getMetadata().getModifiers();
				Query query = createQuery(entityManager, modifiers, false);
				@SuppressWarnings("unchecked")
				List<T> list = (List<T>) getResultList(query);
				return new QueryResults<>(list, modifiers, total);
			} else {
				return QueryResults.emptyResults();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.querydsl.core.Fetchable#fetchCount()
	 */
	@Override
	public long fetchCount() {
		return operationContext.withEntityManager(entityManager -> {
			Query query = createQuery(entityManager, null, true);
			return (Long) query.getSingleResult();
		});
	}

	private Object getSingleResult(Query query) {
		if (projection != null) {
			Object result = query.getSingleResult();
			if (result != null) {
				if (!result.getClass().isArray()) {
					result = new Object[] { result };
				}
				return projection.newInstance((Object[]) result);
			} else {
				return null;
			}
		} else {
			return query.getSingleResult();
		}
	}

	private List<?> getResultList(Query query) {
		if (projection != null) {
			List<?> results = query.getResultList();
			List<Object> rv = new ArrayList<>(results.size());
			for (Object o : results) {
				if (o != null) {
					if (!o.getClass().isArray()) {
						o = new Object[] { o };
					}
					rv.add(projection.newInstance((Object[]) o));
				} else {
					rv.add(null);
				}
			}
			return rv;
		} else {
			return query.getResultList();
		}
	}

	protected Query createQuery(EntityManager entityManager) {
		return createQuery(null, entityManager, getMetadata().getModifiers(), false);
	}

	protected Query createQuery(JPQLTemplates jpqlTemplates, EntityManager entityManager) {
		return createQuery(jpqlTemplates, entityManager, getMetadata().getModifiers(), false);
	}

	protected Query createQuery(EntityManager entityManager, QueryModifiers modifiers, boolean forCount) {
		return createQuery(null, entityManager, modifiers, forCount);
	}

	protected Query createQuery(JPQLTemplates jpqlTemplates, EntityManager entityManager, QueryModifiers modifiers,
			boolean forCount) {

		// configure query
		final QueryDslResolutionContext context = QueryDslResolutionContext.create(operationContext, getMetadata());
		context.addExpressionResolvers(getQueryConfiguration().getExpressionResolvers());

		configureQuery(this, getQueryConfiguration(), context);

		final JPQLTemplates templates = (jpqlTemplates != null) ? jpqlTemplates
				: JPAProvider.getTemplates(entityManager);

		// serialize query
		JPQLSerializer serializer = serialize(templates, entityManager, forCount);
		final String queryString = serializer.toString();

		// trace
		operationContext.trace(queryString.replace('\n', ' '));

		final Query query = entityManager.createQuery(queryString);
		JPAUtil.setConstants(query, serializer.getConstantToLabel(), getMetadata().getParams());

		// check restrictions
		if (modifiers != null && modifiers.isRestricting()) {
			Integer limit = modifiers.getLimitAsInteger();
			Integer offset = modifiers.getOffsetAsInteger();
			if (limit != null) {
				query.setMaxResults(limit);
			}
			if (offset != null) {
				query.setFirstResult(offset);
			}
		}

		// check configuration
		queryDefinition.getParameter(JpaQueryHint.QUERY_PARAMETER_HINT)
				.ifPresent(p -> query.setHint(p.getName(), p.getValue()));
		queryDefinition.getParameter(JpaDatastore.QUERY_PARAMETER_LOCK_MODE).ifPresent(p -> query.setLockMode(p));
		queryDefinition.getParameter(JpaDatastore.QUERY_PARAMETER_FLUSH_MODE).ifPresent(p -> query.setFlushMode(p));

		// set transformer, if necessary and possible
		Expression<?> projection = getMetadata().getProjection();
		this.projection = null; // necessary when query is reused

		if (!forCount && projection instanceof FactoryExpression) {
			if (!templates.getQueryHandler().transform(query, (FactoryExpression<?>) projection)) {
				this.projection = (FactoryExpression<?>) projection;
			}
		}

		return query;
	}

	protected JPQLSerializer createSerializer(JPQLTemplates templates, EntityManager entityManager) {
		return new JPQLSerializer(templates, entityManager);
	}

	protected JPQLSerializer serialize(JPQLTemplates templates, EntityManager entityManager, boolean forCountRow) {
		return serialize(templates, entityManager, forCountRow, true);
	}

	protected JPQLSerializer serialize(JPQLTemplates templates, EntityManager entityManager, boolean forCountRow,
			boolean validate) {
		if (validate) {
			if (queryMixin.getMetadata().getJoins().isEmpty()) {
				throw new IllegalArgumentException("No sources given");
			}
		}
		JPQLSerializer serializer = createSerializer(templates, entityManager);
		serializer.serialize(queryMixin.getMetadata(), forCountRow, null);
		return serializer;
	}

	/**
	 * Configure JPA query using query definition
	 * @param query JPA query to configure
	 * @param configuration Query configuration
	 * @param parentContext Optional parent context
	 */
	public static void configureQuery(JPQLQuery<?> query, QueryConfiguration configuration,
			QueryDslResolutionContext context) {

		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(configuration, "QueryConfiguration must be not null");

		// filter
		configuration.getFilter().ifPresent(f -> {
			PredicateExpression predicate = context.resolve(f, PredicateExpression.class, context)
					.orElseThrow(() -> new InvalidExpressionException("Failed to resolve filter [" + f + "]"));
			predicate.validate();
			query.where(predicate.getPredicate());
		});

		// sort
		configuration.getSort().ifPresent(s -> {
			OrderSpecifierExpression sort = context.resolve(s, OrderSpecifierExpression.class, context)
					.orElseThrow(() -> new InvalidExpressionException("Failed to resolve sort [" + s + "]"));
			sort.validate();
			query.orderBy(sort.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
		});

		// aggregation
		configuration.getAggregation().ifPresent(a -> {
			QueryDslAggregation aggregation = context.resolve(a, QueryDslAggregation.class, context)
					.orElseThrow(() -> new InvalidExpressionException("Failed to resolve aggregation [" + a + "]"));
			aggregation.validate();
			// group by
			query.groupBy(aggregation.getGroupBys().toArray(new Expression[0]));
			// having
			aggregation.getHaving().ifPresent(h -> query.having(h));
		});
	}

}
