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
package com.holonplatform.datastore.jpa.querydsl.internal.expressions;

import java.util.ArrayList;
import java.util.List;

import com.holonplatform.core.internal.utils.ObjectUtils;
import com.querydsl.core.types.Expression;

/**
 * Default {@link QueryDslProjection} implementation.
 *
 * @since 5.0.0
 */
public class DefaultQueryDslProjection implements QueryDslProjection {

	private static final long serialVersionUID = 5042766337193667275L;

	private final List<Expression<?>> selections = new ArrayList<>();

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.resolvers.QueryDslProjection#getSelection()
	 */
	@Override
	public List<Expression<?>> getSelection() {
		return selections;
	}

	public void addSelection(Expression<?> selection) {
		ObjectUtils.argumentNotNull(selection, "Selection expression must be not null");
		selections.add(selection);
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.Expression#validate()
	 */
	@Override
	public void validate() throws InvalidExpressionException {
		if (getSelection() == null || getSelection().isEmpty()) {
			throw new InvalidExpressionException("Null or empty selection");
		}
	}

}
