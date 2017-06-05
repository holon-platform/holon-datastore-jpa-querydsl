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
package com.holonplatform.datastore.jpa.querydsl;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.datastore.jpa.internal.querydsl.DefaultQueryDslProperty;
import com.querydsl.core.types.Path;

/**
 * A {@link PathProperty} bound to a QueryDSL {@link Path}.
 * 
 * @param <T> Property type
 *
 * @since 4.3.0
 */
public interface QueryDslProperty<T> extends PathProperty<T> {

	/**
	 * Get the QueryDSL property path
	 * @return Property path
	 */
	Path<T> getPath();

	/**
	 * Create a QueryDslProperty using given QueryDSL {@link Path}.
	 * @param <T> Property type
	 * @param path QueryDSL property path
	 * @return A QueryDslPropertyBuilder for property configuration setup
	 */
	static <T> QueryDslPropertyBuilder<T> of(Path<T> path) {
		ObjectUtils.argumentNotNull(path, "Path must be not null");
		return new DefaultQueryDslProperty<>(path);
	}

	/**
	 * {@link QueryDslProperty} builder.
	 * @param <T> Property type
	 */
	public interface QueryDslPropertyBuilder<T> extends Builder<T, QueryDslPropertyBuilder<T>>, QueryDslProperty<T> {

	}

}
