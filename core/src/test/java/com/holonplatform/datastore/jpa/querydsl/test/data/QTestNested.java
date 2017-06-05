package com.holonplatform.datastore.jpa.querydsl.test.data;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.holonplatform.datastore.jpa.querydsl.test.domain.TestNested;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QTestNested is a Querydsl query type for TestNested
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QTestNested extends BeanPath<TestNested> {

	private static final long serialVersionUID = -968106311L;

	private static final PathInits INITS = PathInits.DIRECT2;

	public static final QTestNested testNested = new QTestNested("testNested");

	public final NumberPath<java.math.BigDecimal> nestedDecimalValue = createNumber("nestedDecimalValue",
			java.math.BigDecimal.class);

	public final StringPath nestedStringValue = createString("nestedStringValue");

	public final QSubNested subNested;

	public QTestNested(String variable) {
		this(TestNested.class, forVariable(variable), INITS);
	}

	public QTestNested(Path<? extends TestNested> path) {
		this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
	}

	public QTestNested(PathMetadata metadata) {
		this(metadata, PathInits.getFor(metadata, INITS));
	}

	public QTestNested(PathMetadata metadata, PathInits inits) {
		this(TestNested.class, metadata, inits);
	}

	public QTestNested(Class<? extends TestNested> type, PathMetadata metadata, PathInits inits) {
		super(type, metadata, inits);
		this.subNested = inits.isInitialized("subNested") ? new QSubNested(forProperty("subNested")) : null;
	}

}
