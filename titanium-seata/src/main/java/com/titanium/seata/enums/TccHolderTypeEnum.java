package com.titanium.seata.enums;

import com.titanium.seata.holder.MemoryTccResourceHolder;
import com.titanium.seata.holder.MysqlTccResourceHolder;
import com.titanium.seata.holder.RedisTccResourceHolder;
import com.titanium.seata.holder.TccResourceHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TccHolderTypeEnum {
    MEMORY("memory", MemoryTccResourceHolder.class),
    REDIS("redis", RedisTccResourceHolder.class),
    MYSQL("mysql", MysqlTccResourceHolder.class);

    private String type;
    private Class<? extends TccResourceHolder> clazz;
}
