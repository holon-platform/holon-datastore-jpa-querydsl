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
package com.holonplatform.datastore.jpa.querydsl.internal.resolvers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.ExpressionResolver;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.internal.utils.ClassUtils;
import com.holonplatform.datastore.jpa.jpql.expression.JpaEntity;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.EntityPathExpression;
import com.querydsl.core.types.EntityPath;

/**
 * {@link EntityPathExpression} resolver for {@link DataTarget}s.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryDslDataTargetResolver implements ExpressionResolver<DataTarget, EntityPathExpression> {

	INSTANCE;

	private final static WeakHashMap<ClassLoader, Map<Class, EntityPath>> ENTITY_PATHS = new WeakHashMap<>();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends DataTarget> getExpressionType() {
		return DataTarget.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getResolvedType()
	 */
	@Override
	public Class<? extends EntityPathExpression> getResolvedType() {
		return EntityPathExpression.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression.ExpressionResolverFunction#resolve(com.holonplatform.core.Expression,
	 * com.holonplatform.core.ExpressionResolver.ResolutionContext)
	 */
	@Override
	public Optional<EntityPathExpression> resolve(DataTarget expression, ResolutionContext context)
			throws InvalidExpressionException {

		// intermediate resolution and validation
		DataTarget target = context.resolve(expression, DataTarget.class, context).orElse(expression);
		target.validate();

		// resolve entity class
		JpaEntity<?> expr = context.resolve(expression, JpaEntity.class, context)
				.orElseThrow(() -> new InvalidExpressionException(
						"Failed to resolve expression [" + expression + "] into a JPA entity class"));
		expr.validate();

		return Optional.of(
				EntityPathExpression.create(resolvePath(ClassUtils.getDefaultClassLoader(), expr.getEntityClass())));
	}

	@SuppressWarnings("unchecked")
	public static <T> EntityPath<T> resolvePath(ClassLoader classLoader, Class<? extends T> domainClass) {

		final ClassLoader cl = (classLoader != null) ? classLoader : ClassUtils.getDefaultClassLoader();

		// check cache
		Map<Class, EntityPath> mappings = ENTITY_PATHS.getOrDefault(cl, Collections.emptyMap());
		if (mappings.containsKey(domainClass)) {
			return mappings.get(domainClass);
		}

		String pathClassName = getQueryClassName(domainClass);

		try {
			Class<?> pathClass = Class.forName(pathClassName, true, domainClass.getClassLoader());
			Field field = getStaticFieldOfType(pathClass);

			if (field == null) {
				throw new IllegalStateException("Static field of type " + pathClass.getName() + " not found");
			} else {
				EntityPath entityPath = (EntityPath) field.get(null);
				if (entityPath != null) {
					// cache value
					ENTITY_PATHS.computeIfAbsent(cl, c -> new HashMap<>()).put(domainClass, entityPath);
				}
				return entityPath;
			}

		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Query class " + pathClassName + " not found for domain class " + domainClass.getName(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Failed to get static field value for field", e);
		}
	}

	/**
	 * Try to get a static field inside the given type.
	 * @param type Type
	 * @return First static field found, or <code>null</code> if not found
	 */
	private static Field getStaticFieldOfType(Class<?> type) {
		for (Field field : type.getDeclaredFields()) {
			boolean isStatic = Modifier.isStatic(field.getModifiers());
			boolean hasSameType = type.equals(field.getType());
			if (isStatic && hasSameType) {
				return field;
			}
		}
		Class<?> superclass = type.getSuperclass();
		return Object.class.equals(superclass) ? null : getStaticFieldOfType(superclass);
	}

	/**
	 * Returns the name of the query class for the given domain class.
	 * @param domainClass Domain class
	 * @return Name of query class
	 */
	private static String getQueryClassName(Class<?> domainClass) {
		String simpleClassName = domainClass.getSimpleName();
		return String.format("%s.Q%s%s", domainClass.getPackage().getName(), getClassBase(simpleClassName),
				domainClass.getSimpleName());
	}

	/*
	 * Analyzes the short class name and potentially returns the outer class.
	 */
	private static String getClassBase(String shortName) {
		String[] parts = shortName.split("\\.");
		if (parts.length < 2) {
			return "";
		}
		return parts[0] + "_";
	}

}
