package com.titanium.data.jpa.repository;

import com.titanium.data.jpa.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface BaseRepository<T extends BaseEntity,I> extends JpaRepository<T,I>, JpaSpecificationExecutor<T>, QuerydslPredicateExecutor<I> {
}
