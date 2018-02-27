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
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers.projection;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.Path;
import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.QueryExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.DefaultQueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;

/**
 * Bean projection resolver.
 *
 * @since 5.1.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE - 100)
public enum QueryDslBeanProjectionResolver
		implements QueryDslContextExpressionResolver<BeanProjection, QueryDslProjection> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends BeanProjection> getExpressionType() {
		return BeanProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends QueryDslProjection> getResolvedType() {
		return QueryDslProjection.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<QueryDslProjection> resolve(BeanProjection expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final DefaultQueryDslProjection qp = new DefaultQueryDslProjection();
		final BeanPropertySet<?> bps = BeanIntrospector.get().getPropertySet(expression.getBeanClass());

		List<Path> selection = ((BeanProjection<?>) expression).getSelection().map(s -> Arrays.asList(s)).orElse(null);
		if (selection == null) {
			// use bean property set
			selection = bps.stream().map(p -> (Path) p).collect(Collectors.toList());
		}
		for (Path<?> path : selection) {
			if (QueryExpression.class.isAssignableFrom(path.getClass())) {
				qp.addSelection(
						context.resolveOrFail((QueryExpression<?>) path, QueryDslExpression.class).getExpression());
			}
		}

		return Optional.of(qp);
	}

}
