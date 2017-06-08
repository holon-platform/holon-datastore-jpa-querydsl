package com.holonplatform.datastore.jpa.querydsl.examples;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QTestJpaDomain is a Querydsl query type for TestJpaDomain
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTest extends EntityPathBase<Test> {

	private static final long serialVersionUID = 321412989L;

	private static final PathInits INITS = PathInits.DIRECT2;

	public static final QTest test = new QTest("test");

	public final NumberPath<Long> id = createNumber("id", Long.class);

	public final StringPath name = createString("name");

	public QTest(String variable) {
		this(Test.class, forVariable(variable), INITS);
	}

	public QTest(Path<? extends Test> path) {
		this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
	}

	public QTest(PathMetadata metadata) {
		this(metadata, PathInits.getFor(metadata, INITS));
	}

	public QTest(PathMetadata metadata, PathInits inits) {
		this(Test.class, metadata, inits);
	}

	public QTest(Class<? extends Test> type, PathMetadata metadata, PathInits inits) {
		super(type, metadata, inits);
	}

}
