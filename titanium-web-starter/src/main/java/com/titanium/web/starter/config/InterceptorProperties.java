package com.titanium.web.starter.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义拦截器配置
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class InterceptorProperties {
    /**
     * 是否启用证拦截器, 默认true
     */
    @Builder.Default
    private boolean enable = true;

    /**
     * 拦截器类
     */
    private Class<? extends HandlerInterceptor> interceptor;

    /**
     * 拦截的url
     */
    @Builder.Default
    private List<String> includePatterns = new ArrayList<>();

    /**
     * 排除的url
     */
    @Builder.Default
    private List<String> excludePatterns = new ArrayList<>();

    /**
     * 排序
     */
    @Builder.Default
    private int order = Ordered.LOWEST_PRECEDENCE;

    /**
     * 追加排除url
     */
    public InterceptorProperties addExcludePatterns(String... patterns) {
        excludePatterns.addAll(Arrays.asList(patterns));
        return this;
    }
}
