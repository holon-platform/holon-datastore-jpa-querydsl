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
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.Path;
import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.internal.query.ConstantExpressionProjection;
import com.holonplatform.core.internal.query.QueryProjectionVisitor;
import com.holonplatform.core.internal.query.QueryProjectionVisitor.VisitableQueryProjection;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.Property;
import com.holonplatform.core.query.BeanProjection;
import com.holonplatform.core.query.CountAllProjection;
import com.holonplatform.core.query.FunctionExpression;
import com.holonplatform.core.query.PropertySetProjection;
import com.holonplatform.core.query.QueryExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.DefaultQueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.EntityPathExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslProjection;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.dsl.Wildcard;

/**
 * {@link QueryDslProjection} resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryDslProjectionResolver implements ExpressionResolver<VisitableQueryProjection, QueryDslProjection>,
		QueryProjectionVisitor<QueryDslProjection, QueryDslResolutionContext> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends VisitableQueryProjection> getExpressionType() {
		return VisitableQueryProjection.class;
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
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<QueryDslProjection> resolve(VisitableQueryProjection expression, ResolutionContext context)
			throws InvalidExpressionException {
		// validate
		expression.validate();

		// resolve using visitor
		return Optional.ofNullable(
				(QueryDslProjection) expression.accept(this, QueryDslResolutionContext.checkContext(context)));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.datastore.DataTarget,
	 * java.lang.Object)
	 */
	@Override
	public <T> QueryDslProjection visit(DataTarget<T> projection, QueryDslResolutionContext context) {
		// resolve entity path
		EntityPathExpression<?> expr = context.resolve(projection, EntityPathExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve projection [" + projection + "]"));
		expr.validate();

		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(expr.getEntityPath());
		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.property.PathProperty,
	 * java.lang.Object)
	 */
	@Override
	public <T> QueryDslProjection visit(PathProperty<T> projection, QueryDslResolutionContext context) {
		QueryDslExpression<?> expr = context.resolve(projection, QueryDslExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve projection [" + projection + "]"));
		expr.validate();

		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(expr.getExpression());
		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.internal.query.
	 * ConstantExpressionProjection, java.lang.Object)
	 */
	@Override
	public <T> QueryDslProjection visit(ConstantExpressionProjection<T> projection, QueryDslResolutionContext context) {
		QueryDslExpression<?> expr = context.resolve(projection, QueryDslExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve projection [" + projection + "]"));
		expr.validate();

		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(expr.getExpression());
		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.query.
	 * FunctionExpression, java.lang.Object)
	 */
	@Override
	public <T> QueryDslProjection visit(FunctionExpression<T> projection, QueryDslResolutionContext context) {
		QueryDslExpression<?> expr = context.resolve(projection, QueryDslExpression.class, context)
				.orElseThrow(() -> new InvalidExpressionException("Failed to resolve projection [" + projection + "]"));
		expr.validate();

		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(expr.getExpression());
		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.query.
	 * PropertySetProjection, java.lang.Object)
	 */
	@Override
	public QueryDslProjection visit(PropertySetProjection projection, QueryDslResolutionContext context) {
		final DefaultQueryDslProjection p = new DefaultQueryDslProjection();

		for (Property<?> property : projection.getPropertySet()) {
			if (QueryExpression.class.isAssignableFrom(property.getClass())) {
				QueryDslExpression<?> expr = context
						.resolve((QueryExpression<?>) property, QueryDslExpression.class, context)
						.orElseThrow(() -> new InvalidExpressionException(
								"Failed to resolve projection [" + projection + "]"));
				expr.validate();
				p.addSelection(expr.getExpression());
			}
		}

		return p;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.query.BeanProjection,
	 * java.lang.Object)
	 */
	@Override
	public <T> QueryDslProjection visit(BeanProjection<T> projection, QueryDslResolutionContext context) {
		final DefaultQueryDslProjection qp = new DefaultQueryDslProjection();
		final BeanPropertySet<T> bps = BeanIntrospector.get().getPropertySet(projection.getBeanClass());

		List<Path> selection = projection.getSelection().map(s -> Arrays.asList(s))
				.orElse(bps.stream().map(p -> (Path) p).collect(Collectors.toList()));
		for (Path<?> path : selection) {
			if (QueryExpression.class.isAssignableFrom(path.getClass())) {
				QueryDslExpression<?> expr = context
						.resolve((QueryExpression<?>) path, QueryDslExpression.class, context)
						.orElseThrow(() -> new InvalidExpressionException(
								"Failed to resolve projection [" + projection + "]"));
				expr.validate();

				qp.addSelection(expr.getExpression());
			}
		}

		return qp;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.query.QueryProjectionVisitor#visit(com.holonplatform.core.query.
	 * CountAllProjection, java.lang.Object)
	 */
	@Override
	public QueryDslProjection visit(CountAllProjection projection, QueryDslResolutionContext context) {
		DefaultQueryDslProjection p = new DefaultQueryDslProjection();
		p.addSelection(Wildcard.count);
		return p;
	}
}
