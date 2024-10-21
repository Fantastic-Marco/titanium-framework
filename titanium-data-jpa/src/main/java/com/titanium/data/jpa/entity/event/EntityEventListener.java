package com.titanium.data.jpa.entity.event;

import jakarta.persistence.*;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * 实体状态监听器
 */
public class EntityEventListener {
    private static List<EntityEventHandler> handlers;
    private static Map<EntityEventHandler, Class> handlerTargetMap;

    static {
        // SPI 加载
        ServiceLoader<EntityEventHandler> loader = ServiceLoader.load(EntityEventHandler.class);
        handlers = loader.stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(EntityEventHandler::getOrder))
                .collect(Collectors.toList());
        //获取每个handler的实体类型
        handlers.forEach(handler -> {
            // 获取实现类的直接超类的泛型信息
            Type genericSuperclass = handler.getClass().getGenericSuperclass();

            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;

                // 获取第一个类型参数（这里的T）
                Type typeArgument = parameterizedType.getActualTypeArguments()[0];

                if (typeArgument instanceof Class) {
                    Class<?> typeArgumentClass = (Class<?>) typeArgument;
                    handlerTargetMap.put(handler, typeArgumentClass);
                }
            }
        });
    }

    /**
     * 插入之前的处理
     * @param entity
     */
    @PrePersist
    public void onPrePersist(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPrePersist(entity));
    }

    /**
     * 插入之后的处理
     * @param entity
     */
    @PostPersist
    public void onPostPersist(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPostPersist(entity));
    }

    /**
     * 更新之前的处理
     * @param entity
     */
    @PreUpdate
    public void onPreUpdate(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPreUpdate(entity));
    }

    /**
     * 更新之后的处理
     * @param entity
     */
    @PostUpdate
    public void onPostUpdate(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPostUpdate(entity));
    }

    /**
     * 删除之前的处理
     * @param entity
     */
    @PreRemove
    public void onPreRemove(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPreRemove(entity));
    }

    /**
     * 删除之后的处理
     * @param entity
     */
    @PostRemove
    public void onPostRemove(Object entity) {
        handlers.stream()
                .filter(handler -> handlerTargetMap.get(handler).isAssignableFrom(entity.getClass()))
                .forEach(h -> h.onPostRemove(entity));
    }


}
