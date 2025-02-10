package com.titanium.seata.tcc;

import com.titanium.seata.action.AbstractTccAction;
import io.seata.spring.annotation.ScannerChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义TccAction扫描器
 */
@Slf4j
public class TitaniumScannerChecker implements ScannerChecker {
    private static ConcurrentHashMap<Object, Boolean> tccActionMap = new ConcurrentHashMap<>();

    public static Boolean check(Object bean) {
        if (tccActionMap.containsKey(bean)) {
            return tccActionMap.get(bean);
        }
        boolean tccAction = isTccAction(bean);
        tccActionMap.put(bean, tccAction);
        return tccAction;
    }

    /**
     * Do check
     * @param bean        the bean
     * @param beanName    the bean name
     * @param beanFactory the bean factory
     * @return the boolean: true=need scan | false=do not scan
     * @throws Exception the exception
     */
    @Override
    public boolean check(Object bean, String beanName, @Nullable ConfigurableListableBeanFactory beanFactory) throws Exception {
        if (bean instanceof AbstractTccAction) {
            boolean tccAction = check(bean);
            if (tccAction) {
                log.info("{} is tcc action,approve by titanium tcc scanner checker", beanName);
                TitaniumTccResourceHolder.put(bean);
            }
            return tccAction;
        } else {
            //不是自己的扫描范围，返回true，不然会影响其他扫描器
            //@see GlobalTransactionScanner#doCheckers
            return true;
        }
    }

    public static boolean isTccAction(Object bean) {
        if (bean instanceof AbstractTccAction) {
            //只要是继承自AbstractTccAction的类，都认为是TccAction
//            boolean isTccAnnotationAction = false;
//            for (Method method : bean.getClass().getMethods()) {
//                if (method.isAnnotationPresent(TwoPhaseBusinessAction.class)) {
//                    isTccAnnotationAction = true;
//                    break;
//                }
//            }
//            return !isTccAnnotationAction;
            return true;
        }
        return false;
    }
}
