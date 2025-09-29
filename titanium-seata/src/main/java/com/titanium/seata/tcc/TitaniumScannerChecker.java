package com.titanium.seata.tcc;

import com.titanium.seata.action.AbstractTccAction;
import io.seata.spring.annotation.ScannerChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义TccAction扫描器
 * 用于识别和处理TCC操作类，确保只有符合条件的类才会被Seata框架处理
 */
@Slf4j
public class TitaniumScannerChecker implements ScannerChecker {
    /**
     * TCC操作类缓存映射
     * 用于存储已检查过的Bean及其是否为TCC操作类的结果，避免重复检查
     */
    private static ConcurrentHashMap<Object, Boolean> tccActionMap = new ConcurrentHashMap<>();

    /**
     * 检查指定的Bean是否为TCC操作类
     * @param bean 待检查的Bean对象
     * @return true=是TCC操作类，false=不是TCC操作类
     */
    public static Boolean check(Object bean) {
        // 先从缓存中查找是否已检查过该Bean
        if (tccActionMap.containsKey(bean)) {
            return tccActionMap.get(bean);
        }
        // 检查Bean是否为TCC操作类
        boolean tccAction = isTccAction(bean);
        // 将检查结果存入缓存
        tccActionMap.put(bean, tccAction);
        return tccAction;
    }

    /**
     * 执行检查逻辑
     * @param bean        待检查的Bean对象
     * @param beanName    Bean名称
     * @param beanFactory Bean工厂
     * @return true=需要扫描处理，false=不需要扫描处理
     * @throws Exception 检查过程中可能抛出的异常
     */
    @Override
    public boolean check(Object bean, String beanName, @Nullable ConfigurableListableBeanFactory beanFactory) throws Exception {
        // 判断Bean是否为TCC操作类实例
        if (bean instanceof AbstractTccAction) {
            // 检查是否为TCC操作类
            boolean tccAction = check(bean);
            if (tccAction) {
                log.info("{} 是TCC操作类，已通过 titanium 框架TCC扫描器检查", beanName);
                // 将Bean添加到TCC资源持有者中
                TitaniumTccResourceHolder.put(bean);
            }
            return tccAction;
        } else {
            // 不在本扫描器处理范围内的Bean，返回true
            // 避免影响其他扫描器的正常工作
            //@see GlobalTransactionScanner#doCheckers
            return true;
        }
    }

    /**
     * 判断对象是否为TCC操作类
     * @param bean 待判断的对象
     * @return true=是TCC操作类，false=不是TCC操作类
     */
    public static boolean isTccAction(Object bean) {
        // 判断是否继承自AbstractTccAction类
        if (bean instanceof AbstractTccAction) {
            return true;
        }
        return false;
    }
}