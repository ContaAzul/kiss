package com.marvinformatics.kiss.querydslmockery;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NonUniqueResultException;

import com.marvinformatics.kiss.querydslmockery.impl.AbstractQueryBase;
import com.marvinformatics.kiss.querydslmockery.impl.MockeryQueryTemplates;
import com.mysema.query.DefaultQueryMetadata;
import com.mysema.query.JoinType;
import com.mysema.query.Tuple;
import com.mysema.query.collections.CollQueryMixin;
import com.mysema.query.collections.CollQuerySerializer;
import com.mysema.query.collections.DefaultEvaluatorFactory;
import com.mysema.query.collections.DefaultQueryEngine;
import com.mysema.query.collections.QueryEngine;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.CollectionExpression;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;
import com.mysema.query.types.MapExpression;
import com.mysema.query.types.Operator;
import com.mysema.query.types.Path;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.QTuple;

/**
 * @author Marvin
 */
public class JPQLMockeryQuery extends AbstractQueryBase<JPQLMockeryQuery> {

	private final Map<Expression<?>, Iterable<?>> iterables = new HashMap<Expression<?>, Iterable<?>>();

	private final QueryEngine queryEngine;

	public JPQLMockeryQuery() {
		this(new CollQueryMixin<JPQLMockeryQuery>(new DefaultQueryMetadata()),
				new DefaultQueryEngine(new DefaultEvaluatorFactory(
						MockeryQueryTemplates.DEFAULT)));

		try {
			clearSerializerOperators();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		this.queryMixin.setSelf(this);
	}

	public JPQLMockeryQuery(CollQueryMixin<JPQLMockeryQuery> queryMixin,
			QueryEngine queryEngine) {
		super(queryMixin);
		this.queryEngine = queryEngine;
	}

	public <A> JPQLMockeryQuery bind(Path<A> entity, Iterable<? extends A> col) {
		iterables.put(entity, col);
		queryMixin.getMetadata().addJoin(JoinType.DEFAULT, entity);
		return this;
	}

	private void clearSerializerOperators() throws NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = CollQuerySerializer.class
				.getDeclaredField("OPERATOR_SYMBOLS");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		Map<Operator<?>, String> OPERATOR_SYMBOLS = (Map<Operator<?>, String>) field
				.get(null);
		OPERATOR_SYMBOLS.clear();
	}

	@Override
	public long count() {
		try {
			return queryEngine.count(queryMixin.getMetadata(), iterables);
		} finally {
			reset();
		}
	}

	@Override
	public boolean exists() {
		try {
			return queryEngine.exists(queryMixin.getMetadata(), iterables);
		} finally {
			reset();
		}
	}

	@Override
	public JPQLQuery fetch() {
		return this;
	}

	@Override
	public JPQLQuery from(EntityPath<?>... sources) {
		// seem to be the most reasonable thing to do
		return this;
	}

	@Override
	public <P> JPQLQuery join(CollectionExpression<?, P> target) {
		queryMixin.innerJoin(target);
		return this;
	}

	@Override
	public <P> JPQLQuery join(CollectionExpression<?, P> target, Path<P> alias) {
		queryMixin.innerJoin(target, alias);
		return this;
	}

	@Override
	public <P> JPQLQuery join(EntityPath<P> target) {
		queryMixin.innerJoin(target);
		return this;
	}

	@Override
	public <P> JPQLQuery join(EntityPath<P> target, Path<P> alias) {
		queryMixin.innerJoin(target, alias);
		return this;
	}

	@Override
	public <P> JPQLQuery join(MapExpression<?, P> target) {
		queryMixin.innerJoin(target);
		return this;
	}

	@Override
	public <P> JPQLQuery join(MapExpression<?, P> target, Path<P> alias) {
		queryMixin.innerJoin(target, alias);
		return this;
	}

	@Override
	public <RT> List<RT> list(Expression<RT> projection) {
		try {
			projection = queryMixin.convert(projection);
			queryMixin.addProjection(projection);
			return queryEngine.list(queryMixin.getMetadata(), iterables,
					projection);
		} finally {
			reset();
		}
	}

	@Override
	public boolean notExists() {
		return !exists();
	}

	private void reset() {
		queryMixin.getMetadata().reset();
	}

	@Override
	public Tuple singleResult(Expression<?>... args) {
		return limit(1).uniqueResult(args);
	}

	@Override
	public <RT> RT singleResult(Expression<RT> projection) {
		return limit(1).uniqueResult(projection);
	}

	@Override
	public Tuple uniqueResult(Expression<?>... args) {
		return uniqueResult(new QTuple(args));
	}

	@Override
	public <RT> RT uniqueResult(Expression<RT> projection) {
		try {
			List<RT> resultSet = list(projection);

			if (resultSet.isEmpty())
				return null;

			if (resultSet.size() != 1)
				throw new NonUniqueResultException();

			return resultSet.get(0);
		} finally {
			reset();
		}
	}

	@Override
	public JPQLMockeryQuery where(Predicate... o) {
		return super.where(o);
	}

}