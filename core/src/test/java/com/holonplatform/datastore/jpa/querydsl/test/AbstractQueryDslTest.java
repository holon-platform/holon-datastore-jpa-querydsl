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
package com.holonplatform.datastore.jpa.querydsl.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.holonplatform.core.beans.BeanIntrospector;
import com.holonplatform.core.beans.BeanPropertySet;
import com.holonplatform.core.datastore.DataTarget;
import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.core.datastore.Datastore.OperationResult;
import com.holonplatform.core.datastore.bulk.BulkDelete;
import com.holonplatform.core.datastore.bulk.BulkUpdate;
import com.holonplatform.core.datastore.relational.SubQuery;
import com.holonplatform.core.internal.query.filter.NotFilter;
import com.holonplatform.core.internal.query.filter.NotNullFilter;
import com.holonplatform.core.internal.query.filter.NullFilter;
import com.holonplatform.core.property.PathProperty;
import com.holonplatform.core.property.PropertyBox;
import com.holonplatform.core.property.PropertySet;
import com.holonplatform.core.query.Query;
import com.holonplatform.datastore.jpa.JpaTarget;
import com.holonplatform.datastore.jpa.querydsl.JpaQuery;
import com.holonplatform.datastore.jpa.querydsl.QueryDsl;
import com.holonplatform.datastore.jpa.querydsl.QueryDslProperty;
import com.holonplatform.datastore.jpa.querydsl.QueryDslTarget;
import com.holonplatform.datastore.jpa.querydsl.test.data.CustomFilter;
import com.holonplatform.datastore.jpa.querydsl.test.data.CustomFilterResolver;
import com.holonplatform.datastore.jpa.querydsl.test.data.KeyIs;
import com.holonplatform.datastore.jpa.querydsl.test.data.MoreCustomFilter;
import com.holonplatform.datastore.jpa.querydsl.test.data.MoreCustomFilterResolver;
import com.holonplatform.datastore.jpa.querydsl.test.data.QTestJpaDomain;
import com.holonplatform.datastore.jpa.querydsl.test.data.QTestOtherDomain;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestEnum;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestJpaDomain;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestOtherDomain;

public abstract class AbstractQueryDslTest {

	private final static DataTarget<TestJpaDomain> TARGET = QueryDslTarget.of(QTestJpaDomain.testJpaDomain);

	protected final static PathProperty<Long> KEY = PathProperty.create("key", long.class);
	protected final static PathProperty<String> STR = PathProperty.create("stringValue", String.class);
	protected final static PathProperty<Date> DAT = PathProperty.create("dateValue", Date.class);
	protected final static PathProperty<TestEnum> ENM = PathProperty.create("enumValue", TestEnum.class);

	protected final static PropertySet<?> PROPS = PropertySet.of(KEY, STR, DAT, ENM);

	protected abstract Datastore getDatastore();

	@Test
	@Transactional
	public void testDML() {
		getDatastore().save(TARGET, PropertyBox.builder(KEY, STR).set(KEY, 21L).set(STR, "Test save").build());

		Optional<Long> found = getDatastore().query().target(TARGET).filter(KEY.eq(21L)).findOne(KEY);
		assertTrue(found.isPresent());
		assertEquals(new Long(21), found.get());
	}

	@Test
	public void testQueryProjection() {

		final DataTarget<TestJpaDomain> DataTarget = JpaTarget.of(TestJpaDomain.class);

		long count = getDatastore().query().target(DataTarget).count();
		assertEquals(2, count);

		List<PropertyBox> results = getDatastore().query().target(DataTarget).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());

		List<String> values = getDatastore().query().target(DataTarget)
				.list(PathProperty.create("stringValue", String.class));
		assertNotNull(values);
		assertEquals(2, values.size());

		values = getDatastore().query().target(DataTarget)
				.list(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue));
		assertNotNull(values);
		assertEquals(2, values.size());

		values = getDatastore().query().target(DataTarget)
				.list(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue));
		assertNotNull(values);
		assertEquals(2, values.size());

		Optional<Long> cnt = getDatastore().query().target(DataTarget)
				.findOne(PathProperty.create("stringValue", String.class).count());
		assertTrue(cnt.isPresent());
		assertEquals(new Long(2), cnt.get());
	}

	@Test
	public void testQueryAggregateProjection() {

		final DataTarget<TestJpaDomain> DataTarget = JpaTarget.of(TestJpaDomain.class);

		Optional<Long> key = getDatastore().query().target(DataTarget).findOne(KEY.max());
		assertTrue(key.isPresent());
		assertEquals(new Long(2), key.get());

		key = getDatastore().query().target(DataTarget).findOne(KEY.min());
		assertTrue(key.isPresent());
		assertEquals(new Long(1), key.get());

		Optional<Long> sum = getDatastore().query().target(DataTarget).findOne(KEY.sum());
		assertTrue(sum.isPresent());
		assertEquals(new Long(3), sum.get());

		Optional<Long> count = getDatastore().query().target(DataTarget).findOne(KEY.count());
		assertEquals(new Long(2), count.get());
	}

	@Test
	public void testAvg() {
		Optional<Double> avg = getDatastore().query().target(JpaTarget.of(TestJpaDomain.class)).findOne(KEY.avg());
		assertTrue(avg.isPresent());
		assertEquals(new Double(1.5), avg.get());
	}

	@Test
	public void testMultiSelect() {

		List<PropertyBox> results = getDatastore().query().target(JpaTarget.of(TestJpaDomain.class)).sort(KEY.asc())
				.list(KEY, STR);

		assertNotNull(results);
		assertEquals(2, results.size());

		PropertyBox box = results.get(0);

		assertNotNull(box);

		Long key = box.getValue(KEY);

		assertNotNull(key);
		assertEquals(new Long(1), key);

		String str = box.getValue(STR);
		assertNotNull(str);
		assertEquals("One", str);

	}

	@Test
	public void testQueryRestrictions() {
		Query q = getDatastore().query().target(TARGET);

		List<PropertyBox> results = q.limit(1).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		results = q.limit(2).offset(1).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
	}

	@Test
	public void testQueryStream() {

		List<String> results = getDatastore().query().target(DataTarget.named(TestJpaDomain.class.getName()))
				.sort(KEY.asc()).stream(KEY).map((r) -> r.toString()).collect(Collectors.toList());

		assertNotNull(results);
		assertEquals(2, results.size());

		String res = results.get(0);
		assertNotNull(res);
		assertEquals("1", res);

		List<Long> results2 = getDatastore().query().target(QueryDslTarget.of(QTestJpaDomain.testJpaDomain))
				.sort(KEY.asc()).stream(KEY, STR).map((r) -> r.getValue(KEY)).collect(Collectors.toList());

		Long lng = results2.get(0);
		assertNotNull(lng);
		assertEquals(new Long(1), lng);

	}

	@Test
	public void testQueryFilters() throws ParseException {

		List<PropertyBox> results = getDatastore().query().target(TARGET).filter(new NullFilter(DAT)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		results = getDatastore().query().target(TARGET).filter(new NotNullFilter(DAT)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.dateValue).isNull()).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		results = getDatastore().query().target(TARGET).filter(KEY.eq(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET).filter(KEY.eq(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET).filter(KEY.neq(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		Date testDate = new SimpleDateFormat("dd/MM/yyyy").parse("19/04/2016");

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.dateValue).eq(testDate)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET).filter(KEY.goe(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).gt(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).loe(2L)).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).in(1L, 2L)).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).nin(1L, 2L)).list(PROPS);
		assertNotNull(results);
		assertEquals(0, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).between(1L, 2L)).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue).contains("On")).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue).containsIgnoreCase("on"))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(new NotFilter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).eq(1L))).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		List<String> values = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).gt(1L)
						.and(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue).isNotNull()))
				.list(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue));
		assertNotNull(values);
		assertEquals(1, values.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		values = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).eq(1L)
						.or(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.dateValue).isNotNull()))
				.list(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue));
		assertNotNull(values);
		assertEquals(2, values.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.enumValue).eq(TestEnum.SECOND)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		List<TestEnum> evs = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.enumValue).in(TestEnum.FIRST, TestEnum.SECOND))
				.list(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.enumValue));
		assertNotNull(evs);
		assertEquals(2, evs.size());

	}

	@Test
	public void testQueryNested() {
		List<PropertyBox> results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.nested.nestedStringValue).eq("n1"))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET).filter(
				QueryDslProperty.of(QTestJpaDomain.testJpaDomain.nested.subNested.subnestedStringValue).eq("s2"))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		PathProperty<String> nestedString = PathProperty.create("nested.nestedStringValue", String.class);
		PathProperty<String> subnestedString = PathProperty.create("nested.subNested.subnestedStringValue",
				String.class);

		results = getDatastore().query().target(TARGET).filter(nestedString.eq("n1").and(subnestedString.isNotNull()))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

	}

	@Test
	public void testQuerySorts() {

		List<PropertyBox> results = getDatastore().query().target(TARGET).sort(STR.desc()).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET).sort(STR.desc()).sort(KEY.asc()).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.sort(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).desc()).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.sort(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).desc())
				.sort(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.stringValue).asc()).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());
	}

	@Test
	public void testSubQuery() {

		final DataTarget<TestOtherDomain> QT = JpaTarget.of(TestOtherDomain.class);

		List<PropertyBox> results = getDatastore().query().target(TARGET)
				.filter(SubQuery.create(getDatastore()).target(QT)
						.filter(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.code).isNotNull()
								.and(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.sequence)
										.eq(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key))))
						.exists())
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(SubQuery.create(getDatastore()).target(QT)
						.filter(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.code).isNotNull()
								.and(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.sequence)
										.eq(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key))))
						.notExists())
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key)
						.in(SubQuery.create(getDatastore(), Long.class).target(QT)
								.filter(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.code).isNotNull())
								.select(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.sequence))))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(1), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key)
						.nin(SubQuery.create(getDatastore(), Long.class).target(QT)
								.filter(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.code).isNotNull())
								.select(QueryDslProperty.of(QTestOtherDomain.testOtherDomain.sequence))))
				.list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

	}

	@Test
	public void testCustomQueryFilters() {

		List<PropertyBox> results = getDatastore().query().target(TARGET)
				.filter(new CustomFilter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key))).sort(STR.desc())
				.withExpressionResolver(new CustomFilterResolver()).list(PROPS);
		assertNotNull(results);
		assertEquals(2, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

		results = getDatastore().query().target(TARGET)
				.filter(new MoreCustomFilter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key))).sort(STR.desc())
				.withExpressionResolver(new MoreCustomFilterResolver())
				.withExpressionResolver(new CustomFilterResolver()).list(PROPS);
		assertNotNull(results);
		assertEquals(0, results.size());

	}

	@Test
	public void testBeanQuery() {

		BeanPropertySet<TestJpaDomain> testBeanContext = BeanIntrospector.get().getPropertySet(TestJpaDomain.class);

		assertEquals(12, testBeanContext.size());

		List<PropertyBox> results = getDatastore().query().target(TARGET).filter(KEY.gt(1L)).list(PROPS);
		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(new Long(2), results.get(0).getValue(KEY));

	}

	@Test
	public void testDslQuery() {
		JpaQuery<?> q = getDatastore().create(QueryDsl.class).query();
		q.from(QTestJpaDomain.testJpaDomain);

		q.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).eq(2L));

		List<TestJpaDomain> results = q.select(QTestJpaDomain.testJpaDomain).fetch();

		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(Long.valueOf(2), results.get(0).getKey());

		results = getDatastore().create(QueryDsl.class).query().from(QTestJpaDomain.testJpaDomain)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).eq(2L))
				.list(QTestJpaDomain.testJpaDomain);

		assertNotNull(results);
		assertEquals(1, results.size());
		assertEquals(Long.valueOf(2), results.get(0).getKey());

		long count = getDatastore().create(QueryDsl.class).query().from(QTestJpaDomain.testJpaDomain)
				.filter(QueryDslProperty.of(QTestJpaDomain.testJpaDomain.key).eq(2L)).count();
		assertEquals(1, count);

		q = getDatastore().create(QueryDsl.class).query();
		q.from(QTestJpaDomain.testJpaDomain);
		q.withExpressionResolver(KeyIs.RESOLVER);
		q.filter(new KeyIs(1));
		q.sort(KEY.asc());

		Long res = q.singleResult(QTestJpaDomain.testJpaDomain.key);
		assertEquals(new Long(1), res);
		res = q.uniqueResult(QTestJpaDomain.testJpaDomain.key);
		assertEquals(new Long(1), res);

		q = getDatastore().create(QueryDsl.class).query();
		q.from(QTestJpaDomain.testJpaDomain);
		q.withExpressionResolver(new CustomFilterResolver());

		q.select(QTestJpaDomain.testJpaDomain.key, QTestJpaDomain.testJpaDomain.dateValue);

		JpaQuery<?> cloned = q.clone();
		assertNotNull(cloned);
	}

	@Test
	@Transactional
	@Rollback
	public void testBulk() {
		BulkUpdate upd = getDatastore().bulkUpdate(TARGET);
		upd.set(ENM, TestEnum.THIRD);
		upd.setNull(DAT);
		upd.filter(KEY.loe(1L));

		upd.withExpressionResolver(KeyIs.RESOLVER);

		OperationResult result = upd.execute();

		assertEquals(1, result.getAffectedCount());

		upd = getDatastore().bulkUpdate(TARGET);
		upd.set(ENM, TestEnum.THIRD);
		upd.filter(KEY.eq(1L));
		result = upd.execute();

		assertEquals(1, result.getAffectedCount());

		BulkDelete del = getDatastore().bulkDelete(TARGET).filter(KEY.goe(10L));
		del.withExpressionResolver(KeyIs.RESOLVER);
		result = del.execute();

		assertEquals(0, result.getAffectedCount());

		getDatastore().save(TARGET, PropertyBox.builder(KEY, STR).set(KEY, 99L).set(STR, "Test dml").build());

		del = getDatastore().bulkDelete(TARGET).filter(KEY.gt(98L));
		result = del.execute();

		assertEquals(1, result.getAffectedCount());

	}
}
