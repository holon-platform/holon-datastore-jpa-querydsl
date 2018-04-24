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
package com.holonplatform.datastore.jpa.querydsl.internal.config;

import com.holonplatform.core.datastore.DatastoreCommodityContext.CommodityConfigurationException;
import com.holonplatform.core.datastore.DatastoreCommodityFactory;
import com.holonplatform.datastore.jpa.config.JpaDatastoreCommodityContext;
import com.holonplatform.datastore.jpa.config.JpaDatastoreCommodityFactory;
import com.holonplatform.datastore.jpa.querydsl.QueryDsl;
import com.holonplatform.datastore.jpa.querydsl.internal.DefaultQueryDslCommodity;

/**
 * A {@link DatastoreCommodityFactory} for {@link QueryDsl} integration factory.
 * 
 * @since 5.0.0
 */
public class QueryDslCommodityFactory implements JpaDatastoreCommodityFactory<QueryDsl> {

	private static final long serialVersionUID = -5316993911354578279L;

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreCommodityFactory#getCommodityType()
	 */
	@Override
	public Class<? extends QueryDsl> getCommodityType() {
		return QueryDsl.class;
	}

	/*
	 * (non-Javadoc)
	 * @see com.holonplatform.core.datastore.DatastoreCommodityFactory#createCommodity(com.holonplatform.core.datastore.
	 * DatastoreCommodityContext)
	 */
	@Override
	public QueryDsl createCommodity(final JpaDatastoreCommodityContext context) throws CommodityConfigurationException {
		return new DefaultQueryDslCommodity(context);
	}

}
