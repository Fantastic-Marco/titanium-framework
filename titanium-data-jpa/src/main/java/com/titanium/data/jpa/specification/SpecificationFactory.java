package com.titanium.data.jpa.specification;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Optional;

public class SpecificationFactory {

    /**
     * 模糊查询
     * @param attr
     * @param value
     * @return
     */
    public static Optional<Specification> fullLike(String attr, String value) {
        if (StrUtil.isNotBlank(value)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(attr), "%" + value + "%"));
        }
        return Optional.empty();
    }

    /**
     * 左模糊查询
     * @param attr
     * @param value
     * @return
     */
    public static Optional<Specification> leftLike(String attr, String value) {
        if (StrUtil.isNotBlank(value)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(attr), "%" + value));
        }
        return Optional.empty();
    }

    /**
     * 右模糊查询
     * @param attr
     * @param value
     * @return
     */
    public static Optional<Specification> rightLike(String attr, String value) {
        if (StrUtil.isNotBlank(value)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(attr), value + "%"));
        }
        return Optional.empty();
    }

    /**
     * 等于
     * @param attr
     * @param value
     * @return
     */
    public static Optional<Specification> eq(String attr, Object value) {
        if (value != null && StrUtil.isNotBlank(attr)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(attr), value));
        }
        return Optional.empty();
    }

    /**
     * 不等于
     * @param attr
     * @param value
     * @return
     */
    public static Optional<Specification> ne(String attr, Object value) {
        if (value != null && StrUtil.isNotBlank(attr)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(attr), value));
        }
        return Optional.empty();
    }

    /**
     * 包含
     * @param attr
     * @param values
     * @return
     */
    public static Optional<Specification> in(String attr, Collection<Object> values) {
        if (CollectionUtil.isNotEmpty(values)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(attr)).value(values));
        }
        return Optional.empty();
    }

    /**
     * 包含
     * @param attr
     * @param values
     * @return
     */
    public static Optional<Specification> in(String attr, Object... values) {
        if (values != null && values.length > 0) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.in(root.get(attr)).value(values));
        }
        return Optional.empty();
    }

    /**
     * 不包含
     * @param attr
     * @param values
     * @return
     */
    public static Optional<Specification> notIn(String attr, Collection<Object> values) {
        if (CollectionUtil.isNotEmpty(values)) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.in(root.get(attr)).value(values)));
        }
        return Optional.empty();
    }

    /**
     * 不包含
     * @param attr
     * @param values
     * @return
     */
    public static Optional<Specification> notIn(String attr, Object... values) {
        if (values != null && values.length > 0) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.in(root.get(attr)).value(values)));
        }
        return Optional.empty();
    }

    public static <Y extends Comparable<? super Y>> Optional<Specification<Y>> between(Expression<? extends Y> expression, String attr, Y startValue, Y endValue) {
        if (startValue != null && endValue != null) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.between(expression, startValue, endValue));
        }
        return Optional.empty();
    }

    /**
     * 大于
     * @param value
     * @return
     */
    public static <Y extends Comparable<? super Y>> Optional<Specification> gt(Expression<? extends Y> expression, Y value) {
        if (value != null) {
            return Optional.of((root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(expression, value));
        }
        return Optional.empty();
    }
}
