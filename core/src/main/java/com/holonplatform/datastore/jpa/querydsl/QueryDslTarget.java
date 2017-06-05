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

import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.datastore.jpa.internal.querydsl.DefaultQueryDslTarget;
import com.querydsl.core.types.EntityPath;

/**
 * A {@link DataTarget} using QueryDSL {@link EntityPath} to identify target data structure.
 * 
 * @param <T> Entity path type
 * 
 * @since 5.0.0
 */
public interface QueryDslTarget<T> extends DataTarget<T> {

	/**
	 * Get the EntityPath to be used as query target
	 * @return Target EntityPath
	 */
	EntityPath<T> getEntityPath();

	/**
	 * Create a QueryDslTarget using given <code>entityPath</code>.
	 * @param <T> Entity path type
	 * @param entityPath Target EntityPath
	 * @return DataTarget on given entity path
	 */
	static <T> QueryDslTarget<T> of(EntityPath<T> entityPath) {
		return new DefaultQueryDslTarget<>(entityPath);
	}

}
