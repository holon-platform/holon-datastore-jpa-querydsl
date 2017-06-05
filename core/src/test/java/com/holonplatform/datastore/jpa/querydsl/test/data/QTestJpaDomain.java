package com.holonplatform.datastore.jpa.querydsl.test.data;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.holonplatform.datastore.jpa.querydsl.test.domain.TestEnum;
import com.holonplatform.datastore.jpa.querydsl.test.domain.TestJpaDomain;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.EnumPath;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathInits;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QTestJpaDomain is a Querydsl query type for TestJpaDomain
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QTestJpaDomain extends EntityPathBase<TestJpaDomain> {

	private static final long serialVersionUID = 321412989L;

	private static final PathInits INITS = PathInits.DIRECT2;

	public static final QTestJpaDomain testJpaDomain = new QTestJpaDomain("testJpaDomain");

	public final DatePath<java.util.Date> dateValue = createDate("dateValue", java.util.Date.class);

	public final NumberPath<Double> decimalValue = createNumber("decimalValue", Double.class);

	public final EnumPath<TestEnum> enumValue = createEnum("enumValue", TestEnum.class);

	public final NumberPath<Long> key = createNumber("key", Long.class);

	public final QTestNested nested;

	public final StringPath stringValue = createString("stringValue");

	public QTestJpaDomain(String variable) {
		this(TestJpaDomain.class, forVariable(variable), INITS);
	}

	public QTestJpaDomain(Path<? extends TestJpaDomain> path) {
		this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
	}

	public QTestJpaDomain(PathMetadata metadata) {
		this(metadata, PathInits.getFor(metadata, INITS));
	}

	public QTestJpaDomain(PathMetadata metadata, PathInits inits) {
		this(TestJpaDomain.class, metadata, inits);
	}

	public QTestJpaDomain(Class<? extends TestJpaDomain> type, PathMetadata metadata, PathInits inits) {
		super(type, metadata, inits);
		this.nested = inits.isInitialized("nested") ? new QTestNested(forProperty("nested"), inits.get("nested"))
				: null;
	}

}
