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

import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.PropertySetProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.DefaultQueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;

/**
 * Property set projection resolver.
 *
 * @since 5.1.0
 */
@Priority(Integer.MAX_VALUE - 100)
public enum QueryDslPropertySetProjectionResolver
		implements QueryDslContextExpressionResolver<PropertySetProjection, QueryDslProjection> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends PropertySetProjection> getExpressionType() {
		return PropertySetProjection.class;
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
	@Override
	public Optional<QueryDslProjection> resolve(PropertySetProjection expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final DefaultQueryDslProjection p = new DefaultQueryDslProjection();

		for (Property<?> property : expression.getPropertySet()) {
			if (TypedExpression.class.isAssignableFrom(property.getClass())) {
				final TypedExpression<?> propertyExpression = (TypedExpression<?>) property;
				p.addSelection(context.resolveOrFail(propertyExpression, QueryDslExpression.class).getExpression());
			}
		}

		return Optional.of(p);
	}

}
