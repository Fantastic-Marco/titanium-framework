package com.titanium.common.tenant;

/**
 * 租户拦截上下文持有者
 */
public class TenantContextHolder {
    private static final ThreadLocal<TenantContext> context = new ThreadLocal<>();

    public static TenantContext get() {
        return context.get();
    }

    public static void set(TenantContext tenantContext) {
        context.set(tenantContext);
    }

    public static void clear() {
        context.remove();
    }

}
