package cn.javaer.snippetsbox.springframework.boot.data.jdbc.jooq;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.ResultQuery;
import org.jooq.SelectConditionStep;
import org.jooq.SelectOrderByStep;
import org.jooq.SelectWhereStep;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.jooq.SelectQueryMapper;
import org.simpleflatmapper.jooq.SelectQueryMapperFactory;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.support.ExampleMatcherAccessor;
import org.springframework.data.util.DirectFieldAccessFallbackBeanWrapper;
import org.springframework.data.util.Streamable;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of the {@link JooqRepository} interface.
 *
 * @author cn-src
 */
@Transactional(readOnly = true)
public class SimpleJooqRepository<T, ID> implements JooqRepository<T, ID> {

    private final DSLContext dsl;
    private final JdbcAggregateOperations entityOperations;
    private final JdbcMapper<T> jdbcMapper;
    private final SelectQueryMapper<T> queryMapper;
    private final RelationalPersistentEntity<?> persistentEntity;
    private final Class<T> type;
    private final Table<Record> table;

    public SimpleJooqRepository(final JdbcAggregateOperations entityOperations, final PersistentEntity<T, ?> entity, final DSLContext dsl, final RelationalMappingContext context) {
        this.dsl = dsl;
        this.entityOperations = entityOperations;
        this.jdbcMapper = JdbcMapperFactory.newInstance().ignorePropertyNotFound().newMapper(entity.getType());
        this.queryMapper = SelectQueryMapperFactory.newInstance().ignorePropertyNotFound().newMapper(entity.getType());
        this.persistentEntity = context.getRequiredPersistentEntity(entity.getType());
        this.type = entity.getType();
        this.table = DSL.table(this.persistentEntity.getTableName());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <S extends T> S save(final S instance) {
        return this.entityOperations.save(instance);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public <S extends T> Iterable<S> saveAll(final Iterable<S> entities) {

        return Streamable.of(entities).stream()
                .map(this::save)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findById(final ID id) {
        return Optional.ofNullable(this.entityOperations.findById(id, this.type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean existsById(final ID id) {
        return this.entityOperations.existsById(id, this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll() {
        return this.entityOperations.findAll(this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAllById(final Iterable<ID> ids) {
        return this.entityOperations.findAllById(ids, this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return this.entityOperations.count(this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteById(final ID id) {
        this.entityOperations.deleteById(id, this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void delete(final T instance) {
        this.entityOperations.delete(instance, this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAll(final Iterable<? extends T> entities) {
        //noinspection unchecked
        entities.forEach(it -> this.entityOperations.delete(it, (Class<T>) it.getClass()));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAll() {
        this.entityOperations.deleteAll(this.type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<T> findAll(final Sort sort) {
        final SelectWhereStep<Record> step = this.dsl.selectFrom(this.table);
        this.sortStep(step, sort);
        return this.toList(step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<T> findAll(final Pageable pageable) {
        final SelectWhereStep<Record> step = this.dsl.selectFrom(this.table);
        this.pageableStep(step, pageable);
        return new PageImpl<>(this.toList(step), pageable, this.count());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Optional<S> findOne(final Example<S> example) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        return this.toOne(step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Iterable<S> findAll(final Example<S> example) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        return this.toList(step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Iterable<S> findAll(final Example<S> example, final Sort sort) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        this.sortStep(step, sort);
        return this.toList(step);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        final SelectConditionStep<Record> step = this.dsl.selectFrom(this.table).where();
        this.exampleStep(step, example);
        this.pageableStep(step, pageable);

        final SelectConditionStep<Record1<Integer>> countStep = this.dsl.selectCount().from(this.table).where();
        this.exampleStep(countStep, example);
        final int count = this.dsl.fetchCount(countStep);

        return new PageImpl<>(this.toList(step), pageable, count);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> long count(final Example<S> example) {
        final SelectConditionStep<Record1<Integer>> countStep = this.dsl.selectCount().from(this.table).where();
        this.exampleStep(countStep, example);
        return this.dsl.fetchCount(countStep);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends T> boolean exists(final Example<S> example) {
        return this.count(example) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findAll(final QueryStep queryStep) {
        return this.queryMapper.asList(queryStep.step(this.dsl));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<T> findOne(final QueryStep queryStep) {
        return Optional.ofNullable(DataAccessUtils.nullableSingleResult(this.queryMapper.asList(queryStep.step(this.dsl))));
    }

    private <S extends T> void exampleStep(final SelectConditionStep<? extends Record> step, final Example<S> example) {
        final ExampleMatcher matcher = example.getMatcher();
        final ExampleMatcherAccessor exampleAccessor = new ExampleMatcherAccessor(matcher);
        final DirectFieldAccessFallbackBeanWrapper beanWrapper = new DirectFieldAccessFallbackBeanWrapper(example.getProbe());

        for (final RelationalPersistentProperty persistentProperty : this.persistentEntity) {
            final String propertyName = persistentProperty.getName();
            if (exampleAccessor.isIgnoredPath(propertyName)) {
                continue;
            }
            final ExampleMatcher.PropertyValueTransformer transformer = exampleAccessor.getValueTransformerForPath(propertyName);
            final Optional<Object> optionalValue = transformer
                    .apply(Optional.ofNullable(beanWrapper.getPropertyValue(propertyName)));
            final String columnName = persistentProperty.getColumnName();

            final Condition condition;
            if (optionalValue.isPresent()) {
                condition = DSL.field(columnName).eq(optionalValue.get());
            }
            else if (exampleAccessor.getNullHandler().equals(ExampleMatcher.NullHandler.INCLUDE)) {
                condition = DSL.field(columnName).isNull();
            }
            else {
                continue;
            }

            if (matcher.isAllMatching()) {
                step.and(condition);
            }
            else {
                step.or(condition);
            }
        }
    }

    private void pageableStep(final SelectOrderByStep<Record> step, final Pageable pageable) {
        this.sortStep(step, pageable.getSort());
        step.offset(pageable.getOffset())
                .limit(pageable.getPageSize());
    }

    private void sortStep(final SelectOrderByStep<Record> step, final Sort sort) {
        if (sort.isSorted()) {
            //noinspection rawtypes
            final SortField[] fields = sort.map(it -> it.isAscending() ? DSL.field(it.getProperty()).asc()
                    : DSL.field(it.getProperty()).desc()).toList().toArray(new SortField[0]);
            step.orderBy(fields);
        }
    }

    private <S extends T> List<S> toList(final ResultQuery<Record> rq) {
        try (final ResultSet rs = rq.fetchResultSet()) {
            //noinspection unchecked
            return (List<S>) this.jdbcMapper.stream(rs).collect(Collectors.toList());
        }
        catch (final SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }

    private <S extends T> Optional<S> toOne(final ResultQuery<Record> rq) {
        try (final ResultSet rs = rq.fetchResultSet()) {
            //noinspection unchecked
            return Optional.ofNullable(DataAccessUtils.nullableSingleResult(((JdbcMapper<S>) this.jdbcMapper).stream(rs).collect(Collectors.toList())));
        }
        catch (final SQLException e) {
            throw new InvalidResultSetAccessException(e);
        }
    }
}
