package com.holonplatform.datastore.jpa.querydsl.test.data;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.holonplatform.datastore.jpa.querydsl.test.domain.TestOtherDomain;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QTestOtherDomain is a Querydsl query type for TestOtherDomain
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTestOtherDomain extends EntityPathBase<TestOtherDomain> {

	private static final long serialVersionUID = 1872002930L;

	public static final QTestOtherDomain testOtherDomain = new QTestOtherDomain("testOtherDomain");

	public final StringPath code = createString("code");

	public final NumberPath<Long> sequence = createNumber("sequence", Long.class);

	public QTestOtherDomain(String variable) {
		super(TestOtherDomain.class, forVariable(variable));
	}

	public QTestOtherDomain(Path<? extends TestOtherDomain> path) {
		super(path.getType(), path.getMetadata());
	}

	public QTestOtherDomain(PathMetadata metadata) {
		super(TestOtherDomain.class, metadata);
	}

}
