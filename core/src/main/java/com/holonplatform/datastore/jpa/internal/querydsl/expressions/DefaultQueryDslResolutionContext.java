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
package com.holonplatform.datastore.jpa.internal.querydsl.expressions;

import java.util.Optional;

import javax.persistence.EntityManagerFactory;

import com.holonplatform.core.Expression;
import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.ExpressionResolver.ResolutionContext;
import com.holonplatform.core.ExpressionResolverRegistry;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.datastore.jpa.ORMPlatform;
import com.holonplatform.datastore.jpa.context.JpaContext;
import com.holonplatform.datastore.jpa.dialect.ORMDialect;
import com.holonplatform.datastore.jpa.jpql.JPQLValueDeserializer;
import com.holonplatform.datastore.jpa.jpql.JPQLValueSerializer;
import com.querydsl.core.QueryMetadata;

/**
 * Default {@link QueryDslResolutionContext} implementation.
 * 
 * @since 5.0.0
 */
public class DefaultQueryDslResolutionContext implements QueryDslResolutionContext {

	private final JpaContext context;

	private final QueryDslResolutionContext parent;

	private final QueryMetadata queryMetadata;

	private final ExpressionResolverRegistry expressionResolverRegistry = ExpressionResolverRegistry.create();

	public DefaultQueryDslResolutionContext(JpaContext context, QueryMetadata queryMetadata) {
		super();
		ObjectUtils.argumentNotNull(context, "JpaContext must be not null");
		ObjectUtils.argumentNotNull(queryMetadata, "QueryMetadata must be not null");

		this.context = context;
		this.queryMetadata = queryMetadata;
		this.parent = null;

		// inherit resolvers
		addExpressionResolvers(context.getExpressionResolvers());
	}

	public DefaultQueryDslResolutionContext(QueryDslResolutionContext parent, QueryMetadata queryMetadata) {
		super();
		ObjectUtils.argumentNotNull(parent, "Parent context must be not null");
		ObjectUtils.argumentNotNull(queryMetadata, "QueryMetadata must be not null");

		this.context = parent;
		this.queryMetadata = queryMetadata;
		this.parent = parent;

		// inherit resolvers
		addExpressionResolvers(context.getExpressionResolvers());
	}

	/**
	 * Get the JPA context.
	 * @return the JPA context
	 */
	protected JpaContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext#childContext()
	 */
	@Override
	public QueryDslResolutionContext childContext() {
		return new DefaultQueryDslResolutionContext(this, queryMetadata);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#getEntityManagerFactory()
	 */
	@Override
	public EntityManagerFactory getEntityManagerFactory() {
		return getContext().getEntityManagerFactory();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#getDialect()
	 */
	@Override
	public ORMDialect getDialect() {
		return getContext().getDialect();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#getORMPlatform()
	 */
	@Override
	public Optional<ORMPlatform> getORMPlatform() {
		return getContext().getORMPlatform();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#getValueSerializer()
	 */
	@Override
	public JPQLValueSerializer getValueSerializer() {
		return getContext().getValueSerializer();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#getValueDeserializer()
	 */
	@Override
	public JPQLValueDeserializer getValueDeserializer() {
		return getContext().getValueDeserializer();
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#trace(java.lang.String)
	 */
	@Override
	public void trace(String jpql) {
		getContext().trace(jpql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.context.JpaContext#traceOperation(java.lang.String)
	 */
	@Override
	public void traceOperation(String operation) {
		getContext().traceOperation(operation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslResolutionContext#getParent()
	 */
	@Override
	public Optional<QueryDslResolutionContext> getParent() {
		return Optional.ofNullable(parent);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.QueryDslResolutionContext#getQueryMetadata()
	 */
	@Override
	public Optional<QueryMetadata> getQueryMetadata() {
		return Optional.ofNullable(queryMetadata);
	}

	// Expression resolvers

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler#getExpressionResolvers()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterable<ExpressionResolver> getExpressionResolvers() {
		return expressionResolverRegistry.getExpressionResolvers();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverHandler#resolve(com.holonplatform.core.Expression,
	 * java.lang.Class, com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	public <E extends Expression, R extends Expression> Optional<R> resolve(E expression, Class<R> resolutionType,
			ResolutionContext context) throws InvalidExpressionException {
		return expressionResolverRegistry.resolve(expression, resolutionType, context);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport#addExpressionResolver(com.holonplatform.core.
	 * ExpressionResolver)
	 */
	@Override
	public <E extends Expression, R extends Expression> void addExpressionResolver(
			ExpressionResolver<E, R> expressionResolver) {
		expressionResolverRegistry.addExpressionResolver(expressionResolver);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.holonplatform.core.ExpressionResolver.ExpressionResolverSupport#removeExpressionResolver(com.holonplatform.
	 * core.ExpressionResolver)
	 */
	@Override
	public <E extends Expression, R extends Expression> void removeExpressionResolver(
			ExpressionResolver<E, R> expressionResolver) {
		expressionResolverRegistry.removeExpressionResolver(expressionResolver);
	}

}
