package com.titanium.data.mybatis.plus.autofill;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.titanium.common.enums.Bool;
import com.titanium.user.context.UserContext;
import com.titanium.user.context.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 字段填充器
 */
@Slf4j
@Component
public class TitaniumMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("TitaniumMetaObjectHandler：开始插入填充...");
        UserContext userContext = UserContextHolder.get();
        if (ObjectUtil.isNotNull(userContext)) {
            this.strictInsertFill(metaObject, "createdTime", LocalDateTime.class, LocalDateTime.now());
            this.strictInsertFill(metaObject, "creatorName", String.class, userContext.getUserName());
            this.strictInsertFill(metaObject, "creatorId", Long.class, userContext.getUserId());
            this.strictInsertFill(metaObject, "deleted", Integer.class, Bool.NO.getCode());
        }
    }

    /**
     * 更新填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("TitaniumMetaObjectHandler：开始更新填充...");
        UserContext userContext = UserContextHolder.get();
        if (ObjectUtil.isNotNull(userContext)) {
            this.strictUpdateFill(metaObject, "revisedTime", LocalDateTime.class, LocalDateTime.now());
            this.strictUpdateFill(metaObject, "reviserName", String.class, userContext.getUserName());
            this.strictUpdateFill(metaObject, "reviserId", Long.class, userContext.getUserId());
        }
    }

}
