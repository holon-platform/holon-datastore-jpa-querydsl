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

import java.io.Serializable;

import javax.persistence.LockModeType;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.query.Query.QueryBuildException;
import com.holonplatform.core.query.QueryConfiguration;
import com.holonplatform.datastore.jpa.JpaDatastore;
import com.holonplatform.datastore.jpa.JpaQueryHint;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.OrderSpecifierExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.PredicateExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslAggregation;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.AbstractJPAQuery;

/**
 * QueryDSL utility class.
 *
 * @since 5.0.0
 */
public final class QueryDSLUtils implements Serializable {

	private static final long serialVersionUID = 3955243809332574765L;

	private QueryDSLUtils() {
	}

	/**
	 * Configure JPA query using query definition
	 * @param query JPA query to configure
	 * @param configuration Query configuration
	 * @param parentContext Optional parent context
	 */
	public static void configureQueryFromDefinition(JPQLQuery<?> query, QueryConfiguration configuration,
			QueryDslResolutionContext parentContext) {

		ObjectUtils.argumentNotNull(query, "Query must be not null");
		ObjectUtils.argumentNotNull(configuration, "QueryConfiguration must be not null");

		final QueryDslResolutionContext context = QueryDslResolutionContext.create(parentContext, configuration,
				query.getMetadata());

		// parameters
		processQueryParameters(query, configuration);

		// filter
		configuration.getFilter().ifPresent(f -> {
			PredicateExpression predicate = context.resolve(f, PredicateExpression.class, context)
					.orElseThrow(() -> new QueryBuildException("Failed to resolve filter [" + f + "]"));
			predicate.validate();
			query.where(predicate.getPredicate());
		});

		// sort
		configuration.getSort().ifPresent(s -> {
			OrderSpecifierExpression sort = context.resolve(s, OrderSpecifierExpression.class, context)
					.orElseThrow(() -> new QueryBuildException("Failed to resolve sort [" + s + "]"));
			sort.validate();
			query.orderBy(sort.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
		});

		// aggregation
		configuration.getAggregation().ifPresent(a -> {
			QueryDslAggregation aggregation = context.resolve(a, QueryDslAggregation.class, context)
					.orElseThrow(() -> new QueryBuildException("Failed to resolve aggregation [" + a + "]"));
			aggregation.validate();
			// group by
			query.groupBy(aggregation.getGroupBys().toArray(new Expression[0]));
			// having
			aggregation.getHaving().ifPresent(h -> query.having(h));
		});

		// limit and offset
		configuration.getLimit().ifPresent((l) -> query.limit(l));
		configuration.getOffset().ifPresent((o) -> query.offset(o));
	}

	/**
	 * Check if some known parameter is setted in query definition and apply behaviour to query
	 * @param query Query
	 * @param configuration Query configuration
	 */
	@SuppressWarnings("rawtypes")
	private static void processQueryParameters(JPQLQuery<?> query, QueryConfiguration configuration) {

		ObjectUtils.argumentNotNull(query, "Query must be not null");

		if (query instanceof AbstractJPAQuery) {
			configuration.getParameter(JpaQueryHint.QUERY_PARAMETER_HINT, JpaQueryHint.class)
					.ifPresent(p -> ((AbstractJPAQuery) query).setHint(p.getName(), p.getValue()));
			configuration.getParameter(JpaDatastore.QUERY_PARAMETER_LOCK_MODE, LockModeType.class)
					.ifPresent(p -> ((AbstractJPAQuery) query).setLockMode(p));
		}
	}

}
