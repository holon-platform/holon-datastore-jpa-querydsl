# Holon Platform JPA Datastore - QueryDSL integration

> Latest release: [5.1.0](#obtain-the-artifacts)

This module provides a set of [QueryDSL](http://www.querydsl.com) integration features for the [Holon Platform JPA Datastore module](https://github.com/holon-platform/holon-datastore-jpa).

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

See the [module reference guide](https://docs.holon-platform.com/current/reference/holon-datastore-jpa-querydsl.html) for detailed information.  

## Code structure

See [Holon Platform code structure and conventions](https://github.com/holon-platform/platform/blob/master/CODING.md) to learn about the _"real Java API"_ philosophy with which the project codebase is developed and organized.

## Getting started

### System requirements

The Holon Platform is built using __Java 8__, so you need a JRE/JDK version 8 or above to use the platform artifacts.

 __QueryDSL JPA version 4.x.x__ is required.

### Releases

See [releases](https://github.com/holon-platform/holon-datastore-jpa-querydsl/releases) for the available releases. Each release tag provides a link to the closed issues.

### Obtain the artifacts

The [Holon Platform](https://holon-platform.com) is open source and licensed under the [Apache 2.0 license](LICENSE.md). All the artifacts (including binaries, sources and javadocs) are available from the [Maven Central](https://mvnrepository.com/repos/central) repository.

The Maven __group id__ for this module is `com.holon-platform.jpa` and a _BOM (Bill of Materials)_ is provided to obtain the module artifacts:

_Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform.jpa</groupId>
        <artifactId>holon-datastore-jpa-querydsl-bom</artifactId>
        <version>5.1.0</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Using the Platform BOM

The [Holon Platform](https://holon-platform.com) provides an overall Maven _BOM (Bill of Materials)_ to easily obtain all the available platform artifacts:

_Platform Maven BOM:_
```xml
<dependencyManagement>
    <dependency>
        <groupId>com.holon-platform</groupId>
        <artifactId>bom</artifactId>
        <version>${platform-version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencyManagement>
```

See the [Artifacts list](#artifacts-list) for a list of the available artifacts of this module.

### Build from sources

You can build the sources using Maven (version 3.3.x or above is recommended) like this: 

`mvn clean install`

## Getting help

* Check the [platform documentation](https://docs.holon-platform.com/current/reference) or the specific [module documentation](https://docs.holon-platform.com/current/reference/holon-datastore-jpa-querydsl.html).

* Ask a question on [Stack Overflow](http://stackoverflow.com). We monitor the [`holon-platform`](http://stackoverflow.com/tags/holon-platform) tag.

* Report an [issue](https://github.com/holon-platform/holon-datastore-jpa-querydsl/issues).

* A [commercial support](https://holon-platform.com/services) is available too.

## Examples

See the [Holon Platform examples](https://github.com/holon-platform/holon-examples) repository for a set of example projects.

## Contribute

See [Contributing to the Holon Platform](https://github.com/holon-platform/platform/blob/master/CONTRIBUTING.md).

[![Gitter chat](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/holon-platform/contribute?utm_source=share-link&utm_medium=link&utm_campaign=share-link) 
Join the __contribute__ Gitter room for any question and to contact us.

## License

All the [Holon Platform](https://holon-platform.com) modules are _Open Source_ software released under the [Apache 2.0 license](LICENSE).

## Artifacts list

Maven _group id_: `com.holon-platform.jpa`

Artifact id | Description
----------- | -----------
`holon-datastore-jpa-querydsl` | __JPA__ `Datastore` [QueryDSL](http://www.querydsl.com) integration
`holon-starter-jpa-querydsl-hibernate` | __Spring Boot__ _starter_ for JPA stack and Datastore auto-configuration with [QueryDSL](http://www.querydsl.com) integration using [Hibernate](http://hibernate.org/orm) ORM
`holon-starter-jpa-querydsl-eclipselink` | __Spring Boot__ _starter_ for JPA stack and Datastore auto-configuration with [QueryDSL](http://www.querydsl.com) integration using [EclipseLink](http://www.eclipse.org/eclipselink) ORM
`holon-datastore-jpa-querydsl-bom` | Bill Of Materials
`documentation-datastore-jpa-querydsl` | Documentation


