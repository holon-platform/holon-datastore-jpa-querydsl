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

import java.util.List;
import java.util.Optional;

import javax.annotation.Priority;

import com.holonplatform.core.Expression.InvalidExpressionException;
import com.holonplatform.core.TypedExpression;
import com.holonplatform.core.query.QueryFunction;
import com.holonplatform.core.query.QueryFunction.Avg;
import com.holonplatform.core.query.QueryFunction.Count;
import com.holonplatform.core.query.QueryFunction.Max;
import com.holonplatform.core.query.QueryFunction.Min;
import com.holonplatform.core.query.QueryFunction.Sum;
import com.holonplatform.core.query.StringFunction.Lower;
import com.holonplatform.core.query.StringFunction.Upper;
import com.holonplatform.core.query.TemporalFunction.CurrentDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDate;
import com.holonplatform.core.query.TemporalFunction.CurrentLocalDateTime;
import com.holonplatform.core.query.TemporalFunction.CurrentTimestamp;
import com.holonplatform.core.query.TemporalFunction.Day;
import com.holonplatform.core.query.TemporalFunction.Hour;
import com.holonplatform.core.query.TemporalFunction.Month;
import com.holonplatform.core.query.TemporalFunction.Year;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslContextExpressionResolver;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslExpression;
import com.holonplatform.datastore.jpa.querydsl.internal.expressions.QueryDslResolutionContext;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.Expressions;

/**
 * QueryDsl function expression resolver.
 *
 * @since 5.0.0
 */
@SuppressWarnings("rawtypes")
@Priority(Integer.MAX_VALUE)
public enum QueryDslQueryFunctionResolver
		implements QueryDslContextExpressionResolver<QueryFunction, QueryDslExpression> {

	INSTANCE;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.ExpressionResolver#getExpressionType()
	 */
	@Override
	public Class<? extends QueryFunction> getExpressionType() {
		return QueryFunction.class;
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
	 * @see com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslContextExpressionResolver#resolve(com.
	 * holonplatform.core.Expression,
	 * com.holonplatform.datastore.jpa.internal.querydsl.expressions.QueryDslResolutionContext)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<QueryDslExpression> resolve(QueryFunction expression, QueryDslResolutionContext context)
			throws InvalidExpressionException {

		// validate
		expression.validate();

		final Class<? extends QueryFunction> functionType = expression.getClass();

		Expression<?> functionExpression = null;

		if (CurrentDate.class.isAssignableFrom(functionType))
			functionExpression = Expressions.currentDate();
		if (CurrentLocalDate.class.isAssignableFrom(functionType))
			functionExpression = Expressions.currentDate();
		if (CurrentTimestamp.class.isAssignableFrom(functionType))
			functionExpression = Expressions.currentTimestamp();
		if (CurrentLocalDateTime.class.isAssignableFrom(functionType))
			functionExpression = Expressions.currentTimestamp();

		if (Count.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Long.class, Ops.AggOps.COUNT_AGG,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Avg.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Double.class, Ops.AggOps.AVG_AGG,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Min.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.MIN_AGG,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Max.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.MAX_AGG,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Sum.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(expression.getType(), Ops.AggOps.SUM_AGG,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Lower.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.stringOperation(Ops.LOWER,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Upper.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.stringOperation(Ops.UPPER,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Year.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.YEAR,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Month.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.MONTH,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Day.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.DAY_OF_MONTH,
					resolveFunctionArgument(expression, context).getExpression());
		} else if (Hour.class.isAssignableFrom(functionType)) {
			functionExpression = Expressions.numberOperation(Integer.class, Ops.DateTimeOps.HOUR,
					resolveFunctionArgument(expression, context).getExpression());
		}

		if (functionExpression != null) {
			return Optional.of(QueryDslExpression.create(functionExpression));
		}

		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	private static QueryDslExpression resolveFunctionArgument(QueryFunction expression,
			QueryDslResolutionContext context) {
		List<TypedExpression<? extends Object>> arguments = ((QueryFunction<?, Object>) expression)
				.getExpressionArguments();
		if (arguments == null || arguments.size() < 1) {
			throw new InvalidExpressionException("Missing function argument [" + expression.getClass().getName() + "]");
		}
		return context.resolveOrFail(arguments.get(0), QueryDslExpression.class);
	}

}
