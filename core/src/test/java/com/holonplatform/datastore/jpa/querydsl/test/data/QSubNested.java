package com.holonplatform.datastore.jpa.querydsl.test.data;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

import javax.annotation.Generated;

import com.holonplatform.datastore.jpa.querydsl.test.domain.SubNested;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.StringPath;

/**
 * QSubNested is a Querydsl query type for SubNested
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QSubNested extends BeanPath<SubNested> {

	private static final long serialVersionUID = -1455733049L;

	public static final QSubNested subNested = new QSubNested("subNested");

	public final DateTimePath<java.util.Date> subnestedDateValue = createDateTime("subnestedDateValue",
			java.util.Date.class);

	public final StringPath subnestedStringValue = createString("subnestedStringValue");

	public QSubNested(String variable) {
		super(SubNested.class, forVariable(variable));
	}

	public QSubNested(Path<? extends SubNested> path) {
		super(path.getType(), path.getMetadata());
	}

	public QSubNested(PathMetadata metadata) {
		super(SubNested.class, metadata);
	}

}
