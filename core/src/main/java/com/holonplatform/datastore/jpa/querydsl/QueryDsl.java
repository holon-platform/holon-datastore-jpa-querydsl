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

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.DatastoreCommodity;
import com.querydsl.core.Tuple;
import com.querydsl.core.dml.DeleteClause;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.Expressions;

/**
 * A {@link DatastoreCommodity} for JPA {@link Datastore} QueryDSL integration.
 *
 * @since 5.0.0
 */
public interface QueryDsl extends DatastoreCommodity {

	/**
	 * Create a new query.
	 * @return A new {@link JpaQuery}
	 */
	JpaQuery<?> query();

	/**
	 * Create a new {@link JpaQuery} instance with the given projection.
	 * @param expression projection
	 * @param <T> Expression and query result type
	 * @return A new query with given projection
	 */
	default <T> JpaQuery<T> select(Expression<T> expression) {
		return query().select(expression);
	}

	/**
	 * Create a new {@link JpaQuery} instance with the given projection.
	 * @param expressions projections
	 * @return A new query with given projection
	 */
	default JpaQuery<Tuple> select(Expression<?>... expressions) {
		return query().select(expressions);
	}

	/**
	 * Create a new {@link JpaQuery} instance with the given <em>distinct</em> projection.
	 * @param expression projection
	 * @param <T> Expression and query result type
	 * @return A new query with given projection
	 */
	default <T> JpaQuery<T> selectDistinct(Expression<T> expression) {
		return select(expression).distinct();
	}

	/**
	 * Create a new {@link JpaQuery} instance with the given <em>distinct</em> projection.
	 * @param expressions projection
	 * @return A new query with given projection
	 */
	default JpaQuery<Tuple> selectDistinct(Expression<?>... expressions) {
		return select(expressions).distinct();
	}

	/**
	 * Create a new {@link JpaQuery} instance with the projection one.
	 * @return A new query with given projection
	 */
	default JpaQuery<Integer> selectOne() {
		return select(Expressions.ONE);
	}

	/**
	 * Create a new {@link JpaQuery} instance with the projection zero.
	 * @return A new query with given projection
	 */
	default JpaQuery<Integer> selectZero() {
		return select(Expressions.ZERO);
	}

	/**
	 * Create a new {@link JpaQuery} instance with the given source and projection.
	 * @param from projection and source
	 * @param <T> Projection and source type
	 * @return A new query with given source and projection
	 */
	default <T> JpaQuery<T> selectFrom(EntityPath<T> from) {
		return select(from).from(from);
	}

	/**
	 * Create a new Query with the given source.
	 * @param from Query source
	 * @return A new query with given source
	 */
	default JpaQuery<?> from(EntityPath<?> from) {
		return query().from(from);
	}

	/**
	 * Create a new Query with the given sources.
	 * @param froms Query sources
	 * @return A new query with given source
	 */
	default JpaQuery<?> from(EntityPath<?>... froms) {
		return query().from(froms);
	}

	/**
	 * Create a new UPDATE clause.
	 * @param path Entity to update
	 * @return A new update clause
	 */
	UpdateClause<?> update(EntityPath<?> path);

	/**
	 * Create a new DELETE clause.
	 * @param path Entity to delete from
	 * @return A new delete clause
	 */
	DeleteClause<?> delete(EntityPath<?> path);

}
