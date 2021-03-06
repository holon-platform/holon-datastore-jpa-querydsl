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
package com.holonplatform.datastore.jpa.querydsl.test;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.SharedEntityManagerCreator;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.holonplatform.core.datastore.Datastore;
import com.holonplatform.datastore.jpa.JpaDatastore;
import com.holonplatform.datastore.jpa.querydsl.test.data.KeyIs;
import com.holonplatform.datastore.jpa.querydsl.test.data.UselessResolver;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestJpaDomain;

@Rollback
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaQueryDslDatastoreEclipselink.Config.class)
@DirtiesContext
public class TestJpaQueryDslDatastoreEclipselink extends AbstractQueryDslTest {

	@Configuration
	@EnableTransactionManagement
	protected static class Config {

		@Bean
		public DataSource dataSource() {
			return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("testqdsl1")
					.addScript("test-db-schema.sql").addScript("test-db-data.sql").build();
		}

		@Bean
		public FactoryBean<EntityManagerFactory> entityManagerFactory() {
			LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
			emf.setDataSource(dataSource());

			emf.setJpaVendorAdapter(new EclipseLinkJpaVendorAdapter());

			emf.setPackagesToScan(TestJpaDomain.class.getPackage().getName());
			emf.setPersistenceUnitName("test");

			emf.getJpaPropertyMap().put("eclipselink.weaving", "false");
			return emf;
		}

		@Bean
		public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
			JpaTransactionManager jtm = new JpaTransactionManager(emf);
			jtm.setDataSource(dataSource());
			return jtm;
		}

		@Bean
		public JpaDatastore datastore(EntityManagerFactory emf) throws Exception {
			return JpaDatastore.builder().entityManagerFactory(emf)
					// use spring shared entitymanager to join spring transactions
					.entityManagerInitializer(f -> SharedEntityManagerCreator.createSharedEntityManager(f))
					.autoFlush(true).withExpressionResolver(KeyIs.RESOLVER)
					.withExpressionResolver(new UselessResolver()).build();
		}

	}

	@Autowired
	private JpaDatastore datastore;

	@Override
	protected Datastore getDatastore() {
		return datastore;
	}

	@Override
	public void testAvg() {
	}

}
