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
package com.holonplatform.datastore.jpa.internal.querydsl.resolvers;

import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.Path;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.query.Query.QueryBuildException;
import com.holonplatform.datastore.jpa.internal.expressions.JpaEntity;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * QueryDsl {@link Path} expression resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryDslPathResolver implements ExpressionResolver<Path, QueryDslExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends Path> getExpressionType() {
		return Path.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends QueryDslExpression> getResolvedType() {
		return QueryDslExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<QueryDslExpression> resolve(Path expression, ResolutionContext context)
			throws InvalidExpressionException {

		// intermediate resolution and validation
		Path path = context.resolve(expression, Path.class, context).orElse(expression);
		path.validate();

		// get root
		final QueryDslResolutionContext ctx = QueryDslResolutionContext.checkContext(context);

		com.querydsl.core.types.Path<?> root = getParentRootPath(path, ctx).orElse(getQueryRootPath(path, ctx));

		// get query path from property
		final String queryPath = path.getName();

		try {
			PathBuilder<?> rootPathBuilder = new PathBuilder<>(root.getType(), root.getMetadata());
			PathBuilder<?> pb = rootPathBuilder.get(queryPath, path.getType());

			if (pb == null) {
				throw new QueryBuildException("Cannot get a valid PathBuilder for path expression [" + path + "]");
			}

			return Optional.of(QueryDslExpression.create(pb));
		} catch (Exception e) {
			throw new InvalidExpressionException(e);
		}
	}

	private static Optional<com.querydsl.core.types.Path<?>> getParentRootPath(Path<?> path,
			QueryDslResolutionContext context) {

		Optional<DataTarget> target = path.stream().filter(p -> DataTarget.class.isAssignableFrom(p.getClass()))
				.map(p -> (DataTarget) p).findFirst();

		if (target.isPresent()) {

			JpaEntity<?> entity = context.resolve(target.get(), JpaEntity.class, context)
					.orElseThrow(() -> new InvalidExpressionException("Failed to resolve target [" + target.get()
							+ "] declared as parent of path [" + path + "]"));
			entity.validate();

			QueryDslResolutionContext ctx = context;
			while (ctx != null) {
				final List<JoinExpression> joins = ctx.getQueryMetadata().map(q -> q.getJoins())
						.orElseThrow(() -> new InvalidExpressionException("Missing context query metadata"));
				if (joins != null) {
					for (JoinExpression join : joins) {
						if (join.getTarget() != null && EntityPath.class.isAssignableFrom(join.getTarget().getClass())
								&& entity.getEntityClass() == join.getTarget().getType()) {
							return Optional.of((EntityPath<?>) join.getTarget());
						}
					}
				}
				// parent
				ctx = ctx.getParent().orElse(null);
			}

			throw new InvalidExpressionException("None of the query joins corresponds to the target [" + target.get()
					+ "] declared as parent of path [" + path + "]");

		}

		return Optional.empty();

	}

	private static com.querydsl.core.types.Path<?> getQueryRootPath(Path<?> path, QueryDslResolutionContext context) {
		final List<JoinExpression> joins = context.getQueryMetadata().map(q -> q.getJoins())
				.orElseThrow(() -> new InvalidExpressionException("Missing context query metadata"));
		if (joins != null) {
			for (JoinExpression join : joins) {
				Expression<?> je = join.getTarget();
				if (je != null && EntityPath.class.isAssignableFrom(je.getClass())) {
					return (EntityPath<?>) je;
				}
			}
		}
		throw new InvalidExpressionException("Missing query from clause: cannot resolve path [" + path + "]");
	}

}
