package com.titanium.seata.holder;

import com.titanium.seata.enums.TccStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;

@Slf4j
public class RedisTccResourceHolder implements TccResourceHolder {
    private RedisTemplate redisTemplate;
    private String applicationName;

    public RedisTccResourceHolder(RedisTemplate redisTemplate, String applicationName) {
        this.redisTemplate = redisTemplate;
        this.applicationName = applicationName;
    }

    /**
     * 将资源标记为已占用
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean prepare(String xid, Long branchId) {
        String key = getKey(xid, branchId);
        return redisTemplate.opsForValue().setIfAbsent(key, TccStatusEnum.TRYING.getCode());
    }

    /**
     * 将资源标记为已提交
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean commit(String xid, Long branchId) {
        String key = getKey(xid, branchId);
        return updateIfValueEquals(key, TccStatusEnum.TRYING, TccStatusEnum.CONFIRM);
    }

    /**
     * 将资源标记为已回滚
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean cancel(String xid, Long branchId) {
        return updateIfValueEquals(getKey(xid, branchId), TccStatusEnum.TRYING, TccStatusEnum.CANCEL);
    }

    /**
     * 获取当前资源状态
     * @param xid
     * @param branchId
     * @see TccStatusEnum
     */
    @Override
    public Integer getStatus(String xid, Long branchId) {
        return Integer.valueOf((String) redisTemplate.opsForValue().get(getKey(xid, branchId)));
    }

    /**
     * 删除资源
     * @param xid
     * @param branchId
     * @return
     */
    @Override
    public Boolean delete(String xid, Long branchId) {
        try {
            String key = getKey(xid, branchId);
            return deleteIfValueEquals(key, TccStatusEnum.allowDeleteStatusCodes());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    private String getKey(String xid, Long branchId) {
        return new StringBuilder(applicationName).append(":").append(xid).append(":").append(branchId).toString();
    }

    /**
     * 使用Lua脚本检查key的值是否为targetVal，如果是，则将其设置为newVal。
     * @param key       要检查和更新的键
     * @param oldStatus 目标值
     * @param newStatus 新值
     * @return 如果键的值等于targetVal并被成功更新，返回true；否则返回false。
     */
    private boolean updateIfValueEquals(String key, TccStatusEnum oldStatus, TccStatusEnum newStatus) {
        String targetVal = String.valueOf(oldStatus.getCode());
        String newVal = String.valueOf(newStatus.getCode());
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                "   redis.call('set', KEYS[1], ARGV[2]); " +
                "   return 1; " +
                "else " +
                "   return 0; " +
                "end";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript);
        script.setResultType(Long.class);

        Long result = (Long) redisTemplate.execute(script,
                Collections.singletonList(key),
                targetVal, newVal);

        return result == 1L;
    }

    /**
     * 使用Lua脚本检查key的值是否为多个可能值之一，如果是，则删除该键。
     * @param key            要检查和可能删除的键
     * @param possibleValues 可能的值列表
     * @return 如果键的值为可能值之一并被成功删除，返回true；否则返回false。
     */
    private boolean deleteIfValueEquals(String key, String[] possibleValues) {
        StringBuilder luaScript = new StringBuilder();
        luaScript.append("local value = redis.call('get', KEYS[1])\n");
        luaScript.append("for i, v in ipairs(ARGV) do\n");
        luaScript.append("   if value == v then\n");
        luaScript.append("      redis.call('del', KEYS[1])\n");
        luaScript.append("      return 1\n");
        luaScript.append("   end\n");
        luaScript.append("end\n");
        luaScript.append("return 0");

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(luaScript.toString());
        script.setResultType(Long.class);

        Long result = (Long) redisTemplate.execute(script,
                Collections.singletonList(key),
                (Object[]) possibleValues);

        return result == 1L;
    }
}
