package com.titanium.web.starter.config;

import com.titanium.web.starter.interceptor.SkipAuthInterceptor;
import com.titanium.web.starter.interceptor.UserHandlerInterceptor;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "titanium.web")
public class TitaniumWebProperties implements InitializingBean {
    @Resource
    private ObjectProvider<TitaniumWebCustomizer> provider;

    /**
     * 是否开启日志
     */
    private boolean enableApiLogging = false;

    /**
     * web拦截器配置
     */
    private InterceptorProperties webInterceptor = InterceptorProperties.builder()
            .enable(true)
            .interceptor(UserHandlerInterceptor.class)
            .includePatterns(List.of("/**"))
            .build();

    /**
     * 额外拦截器配置
     */
    private List<InterceptorProperties> additionalInterceptors = new ArrayList<>();

    /**
     * 自定义拦截器配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Accessors(chain = true)
    public static class InterceptorProperties {
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


    /**
     * 用于业务服务自定义配置
     */
    @Override
    public void afterPropertiesSet() {
        provider.ifAvailable(customizer -> customizer.customize(this));
    }
}
