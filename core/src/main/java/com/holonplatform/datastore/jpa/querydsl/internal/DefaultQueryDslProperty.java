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
package com.holonplatform.datastore.jpa.querydsl.internal;

import java.util.function.Consumer;

import com.holonplatform.core.internal.property.AbstractPathProperty;
import com.holonplatform.core.internal.utils.ObjectUtils;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.datastore.jpa.JpaTarget;
import com.holonplatform.datastore.jpa.querydsl.QueryDslProperty;
import com.holonplatform.datastore.jpa.querydsl.QueryDslProperty.QueryDslPropertyBuilder;
import com.querydsl.core.types.Path;

/**
 * Default {@link QueryDslProperty} implementation.
 * 
 * @param <T> Property type
 * 
 * @since 4.5.0
 */
public class DefaultQueryDslProperty<T> extends AbstractPathProperty<T, QueryDslProperty<T>, QueryDslPropertyBuilder<T>>
		implements QueryDslPropertyBuilder<T> {

	private static final long serialVersionUID = 5654271615510770761L;

	/*
	 * Property path (immutable)
	 */
	private final Path<T> path;

	/**
	 * Constructor
	 * @param path Property path (required not null)
	 */
	@SuppressWarnings("null")
	public DefaultQueryDslProperty(Path<T> path) {
		super(pathName(path), (path != null) ? path.getType() : null);
		ObjectUtils.argumentNotNull(path, "Path must be not null");
		this.path = path;
		// set parent
		if (path.getRoot() != null) {
			parent(JpaTarget.of(path.getRoot().getType()));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.property.AbstractProperty#getActualProperty()
	 */
	@Override
	protected QueryDslProperty<T> getActualProperty() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.internal.property.AbstractProperty#getActualBuilder()
	 */
	@Override
	protected QueryDslPropertyBuilder<T> getActualBuilder() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.querydsl.PathProperty#getPath()
	 */
	@Override
	public Path<T> getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.property.CloneableProperty#clone(java.util.function.Consumer)
	 */
	@Override
	public PathProperty<T> clone(Consumer<PathProperty.Builder<T, PathProperty<T>, ?>> builder) {
		return clonePathProperty(new DefaultQueryDslProperty<>(getPath()), builder);
	}

	/**
	 * Query name of given path. A dot-notation pattern is used to express nested properties path.
	 * @param path Path
	 * @return Path query name
	 */
	private static String pathName(Path<?> path) {
		if (path != null) {
			StringBuilder sb = new StringBuilder();
			if (!path.getMetadata().isRoot()) {
				Path<?> parent = path.getMetadata().getParent();
				if (!parent.getMetadata().isRoot()) {
					String parentName = pathName(parent);
					if (parentName != null) {
						sb.append(parentName);
						sb.append(".");
					}
				}
			}
			sb.append(path.getMetadata().getName());
			return sb.toString();
		}
		return null;
	}

}
