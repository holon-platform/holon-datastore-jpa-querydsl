# Holon Platform JPA Datastore - QueryDSL integration

This module provides set of [QueryDSL](http://www.querydsl.com) integration features for the [Holon Platform JPA Datastore module](https://github.com/holon-platform/holon-datastore-jpa).

The main feature provided is the `QueryDsl` Datastore _commodity_, automatically registered with the JPA Datastore when this module is available in classpath.

The `QueryDsl` _commodity_ provides methods to create JPA QueryDSL queries which allow to __mix QueryDSL expressions and predicates with standard platform query expressions__, such as `QueryFilter`, `QuerySort` and `QueryAggregation`.

Example:

```java
Datastore datastore = getDatastore(); // build or obtain a JPA Datastore
QueryDsl queryDslCommodity = datastore.create(QueryDsl.class); // get the QueryDsl commodity

JpaQuery<?> query = queryDslCommodity.selectFrom(QTest.test); // build a QueryDSL query
query.where(QTest.test.id.gt(0)); // QueryDSL predicate
query.filter(NAME.startsWith("a")); // Holon platform Property QueryFilter
query.fetch();
```

Furthermore, this module provides integration classes to use [QueryDSL](http://www.querydsl.com) expressions as Datastore expressions (such as `EntityPath` and `Path` QueryDSL types) and some __Spring Boot__ _starters_ for QueryDSL integration auto-configuration.

See the [module reference guide](https://holon-platform.com/docs/current/reference/holon-datastore-jpa-querydsl.html) for detailed information.  


