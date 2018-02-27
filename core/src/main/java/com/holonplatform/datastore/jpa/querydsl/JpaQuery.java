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
package com.holonplatform.datastore.jpa.querydsl;

import java.util.List;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.query.QueryAggregation;
import com.holonplatform.core.query.QueryBuilder;
import com.holonplatform.core.query.QueryFilter;
import com.holonplatform.core.query.QuerySort;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.MapExpression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.JPQLQuery;

/**
 * QueryDSL JPA query interface.
 * <p>
 * Combines the features of the standard QueryDSL {@link JPQLQuery} with the Holon {@link QueryBuilder}, allowing to
 * seamlessy use the default core query architecture expressions (for example {@link DataTarget}, {@link QueryFilter},
 * {@link QuerySort} and {@link QueryAggregation}) for query definition.
 * </p>
 * <p>
 * Provides some convenience methods to obtain query results providing a projection expression, such as
 * {@link #list(Expression)}, {@link #singleResult(Expression)}, {@link #uniqueResult(Expression)}.
 * </p>
 * <p>
 * The {@link QueryDsl} Datastore commodity can be used to obtain a new {@link JpaQuery} instance through the
 * {@link QueryDsl#query()} method. Example:
 * 
 * <pre>
 * JpaQuery q = getDatastore().create(QueryDsl.class).query();
 * </pre>
 * 
 * @param <T> Query result type
 * 
 * @since 5.1.0
 */
public interface JpaQuery<T> extends QueryBuilder<JpaQuery<T>>, JPQLQuery<T> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> groupBy(Expression<?>... o);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> having(Predicate... o);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> limit(long limit);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> offset(long offset);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> restrict(QueryModifiers modifiers);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> orderBy(OrderSpecifier<?>... o);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> set(ParamExpression<P> param, P value);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> distinct();

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> where(Predicate... o);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> from(EntityPath<?>... sources);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> from(CollectionExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(EntityPath<P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(EntityPath<P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(CollectionExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(CollectionExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(MapExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> innerJoin(MapExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(EntityPath<P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(EntityPath<P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(CollectionExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(CollectionExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(MapExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> join(MapExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(EntityPath<P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(EntityPath<P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(CollectionExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(CollectionExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(MapExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> leftJoin(MapExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(EntityPath<P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(EntityPath<P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(CollectionExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(CollectionExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(MapExpression<?, P> target);

	/**
	 * {@inheritDoc}
	 */
	@Override
	<P> JpaQuery<T> rightJoin(MapExpression<?, P> target, Path<P> alias);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> on(Predicate... condition);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> fetchJoin();

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<T> fetchAll();

	/**
	 * {@inheritDoc}
	 */
	@Override
	<U> JpaQuery<U> select(Expression<U> expr);

	/**
	 * {@inheritDoc}
	 */
	@Override
	JpaQuery<Tuple> select(Expression<?>... exprs);

	/**
	 * Set the query {@link LockModeType}.
	 * @param lockMode Lock mode
	 * @return this
	 */
	JpaQuery<T> setLockMode(LockModeType lockMode);

	/**
	 * Set the query {@link FlushModeType}.
	 * @param flushMode Flush mode
	 * @return this
	 */
	JpaQuery<T> setFlushMode(FlushModeType flushMode);

	/**
	 * Add a query hint.
	 * @param name Query hint name
	 * @param value Query hint value
	 * @return this
	 */
	JpaQuery<T> setHint(String name, Object value);

	/**
	 * Convenience method to execute the query and return results as {@link List}. An empty list is returned when no
	 * result is found.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return Query results {@link List}
	 */
	default <RT> List<RT> list(Expression<RT> projection) {
		return select(projection).fetch();
	}

	/**
	 * Convenience method to execute the query and return a single result. For multiple results, only the first one is
	 * returned.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return The single (first) query result, <code>null</code> if no results available
	 */
	default <RT> RT singleResult(Expression<RT> projection) {
		return select(projection).fetchFirst();
	}

	/**
	 * Convenience method to execute the query and return the result, which is expected to be unique.
	 * @param <RT> Result type
	 * @param projection Projection
	 * @return The unique query result, <code>null</code> if no results available
	 * @throws NonUniqueResultException if there is more than one result
	 */
	default <RT> RT uniqueResult(Expression<RT> projection) {
		return select(projection).fetchOne();
	}

	/**
	 * Shorter for count(*) on selected entity path.
	 * @return Count result
	 */
	default long count() {
		return select(Wildcard.count).fetchCount();
	}

}
