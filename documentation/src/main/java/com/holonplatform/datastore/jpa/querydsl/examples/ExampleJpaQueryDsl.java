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
package com.holonplatform.datastore.jpa.querydsl.examples;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.StringProperty;
import com.holonplatform.datastore.jpa.querydsl.JpaQuery;
import com.holonplatform.datastore.jpa.querydsl.QueryDsl;
import com.holonplatform.datastore.jpa.querydsl.QueryDslProperty;
import com.holonplatform.datastore.jpa.querydsl.QueryDslTarget;

public class ExampleJpaQueryDsl {

	public void target() {
		PropertyBox value = null;
		// tag::target[]
		final QueryDslTarget<Test> TARGET = QueryDslTarget.of(QTest.test); // <1>

		Datastore datastore = getDatastore(); // build or obtain a JPA Datastore

		datastore.refresh(TARGET, value); // <2>
		// end::target[]
	}

	public void property() {
		// tag::property[]
		final QueryDslProperty<Long> ID = QueryDslProperty.of(QTest.test.id); // <1>
		final QueryDslProperty<String> NAME = QueryDslProperty.of(QTest.test.name); // <2>

		Datastore datastore = getDatastore(); // build or obtain a JPA Datastore

		datastore.save(QueryDslTarget.of(QTest.test),
				PropertyBox.builder(ID, NAME).set(ID, 1L).set(NAME, "TestName").build()); // <3>
		// end::property[]
	}

	@SuppressWarnings("unused")
	public void commodity() {
		// tag::commodity[]
		Datastore datastore = getDatastore(); // build or obtain a JPA Datastore

		QueryDsl queryDslCommodity = datastore.create(QueryDsl.class); // <1>

		JpaQuery<?> query = queryDslCommodity.query(); // <2>
		query = queryDslCommodity.selectFrom(QTest.test); // <3>

		queryDslCommodity.update(QTest.test).set(QTest.test.name, "UpdatedName").where(QTest.test.id.eq(1L)).execute(); // <4>
		queryDslCommodity.delete(QTest.test).where(QTest.test.id.loe(1L)).execute(); // <5>
		// end::commodity[]
	}

	@SuppressWarnings("unused")
	public void query() {
		// tag::query[]
		Datastore datastore = getDatastore(); // build or obtain a JPA Datastore

		final PathProperty<Long> ID = QueryDslProperty.of(QTest.test.id); // <1>
		final StringProperty NAME = StringProperty.create("name"); // <2>

		long count = getDatastore().create(QueryDsl.class).query().from(QTest.test)
				.filter(ID.gt(2L).and(NAME.startsWith("n"))).count(); // <3>
		// end::query[]
	}

	@SuppressWarnings("static-method")
	private Datastore getDatastore() {
		return null;
	}

}
