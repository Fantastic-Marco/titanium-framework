package com.titanium.data.jpa.entity.event;

public interface EntityEventHandler<T> {
    /**
     * 排序
     * @return
     */
    Integer getOrder();

    /**
     * 保存之前
     * @param entity
     */
    void onPrePersist(T entity);

    /**
     * 保存之后
     * @param entity
     */
    void onPostPersist(T entity);

    /**
     * 更新之前
     * @param entity
     */
    void onPreUpdate(T entity);

    /**
     * 更新之后
     * @param entity
     */
    void onPostUpdate(T entity);

    /**
     * 删除之前
     * @param entity
     */
    void onPreRemove(T entity);

    /**
     * 删除之后
     * @param entity
     */
    void onPostRemove(T entity);

    /**
     * 逻辑删除之前
     * @param entity
     */
    void onPreLogicDelete(T entity);

    /**
     * 逻辑删除之后
     * @param entity
     */
    void onPostLogicDelete(T entity);

}
