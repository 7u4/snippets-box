package cn.javaer.snippets.box.jooq;

import org.jooq.Condition;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Supplier;

/**
 * @author cn-src
 */
public class ConditionBuilder {
    private final List<Condition> conditions = new ArrayList<>();

    public ConditionBuilder append(final Supplier<Condition> supplier) {
        this.conditions.add(supplier.get());
        return this;
    }

    public <T> ConditionBuilder append(final Function<T, Condition> fun, final T value) {
        if (ObjectUtils.isEmpty(value)) {
            return this;
        }

        this.conditions.add(fun.apply(value));
        return this;
    }

    public ConditionBuilder append(final IntFunction<Condition> fun, final int value) {
        this.conditions.add(fun.apply(value));
        return this;
    }

    public ConditionBuilder append(final LongFunction<Condition> fun, final long value) {
        this.conditions.add(fun.apply(value));
        return this;
    }

    @SafeVarargs
    public final <T> ConditionBuilder append(final Function<T[], Condition> fun, final T... array) {
        if (null == array || array.length == 0) {
            return this;
        }

        this.conditions.add(fun.apply(array));
        return this;
    }

    public ConditionBuilder append(final Function<int[], Condition> fun, final int... array) {
        if (null == array || array.length == 0) {
            return this;
        }

        this.conditions.add(fun.apply(array));
        return this;
    }

    public ConditionBuilder append(final Function<long[], Condition> fun, final long... array) {
        if (null == array || array.length == 0) {
            return this;
        }

        this.conditions.add(fun.apply(array));
        return this;
    }

    public ConditionBuilder append(final BiFunction<LocalDateTime, LocalDateTime, Condition> fun, final LocalDate start, final LocalDate end) {
        if (null == start || null == end) {
            return this;
        }
        final LocalDateTime startTime = start.atStartOfDay();
        final LocalDateTime endTime = end.atTime(LocalTime.MAX);
        this.conditions.add(fun.apply(startTime, endTime));
        return this;
    }

    @Nullable
    public Condition build() {
        if (this.conditions.isEmpty()) {
            return null;
        }
        Condition condition = this.conditions.get(0);
        for (int i = 1, size = this.conditions.size(); i < size; i++) {
            condition = condition.and(this.conditions.get(i));
        }
        return condition;
    }
}
