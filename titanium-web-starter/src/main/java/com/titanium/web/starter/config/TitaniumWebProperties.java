package com.titanium.web.starter.config;

import com.titanium.web.starter.interceptor.TenantHandlerInterceptor;
import com.titanium.web.starter.interceptor.UserHandlerInterceptor;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties(prefix = "titanium.web")
public class TitaniumWebProperties implements InitializingBean {
    @Resource
    private ObjectProvider<TitaniumWebCustomizer> provider;

    /**
     * 是否开启日志
     */
    @Builder.Default
    private boolean enableApiLogging = false;

    /**
     * web拦截器配置
     * 默认开启
     */
    @Builder.Default
    private InterceptorProperties webInterceptor = InterceptorProperties.builder()
            .enable(true)
            .interceptor(UserHandlerInterceptor.class)
            .includePatterns(List.of("/**"))
            .build();

    /**
     * 租户拦截器配置
     * 默认不开启
     */
    @Builder.Default
    private InterceptorProperties tenantInterceptor = InterceptorProperties.builder()
            .enable(false)
            .interceptor(TenantHandlerInterceptor.class)
            .includePatterns(List.of("/**"))
            .build();

    /**
     * 额外拦截器配置
     */
    @Builder.Default
    private List<InterceptorProperties> additionalInterceptors = new ArrayList<>();


    /**
     * 用于业务服务自定义配置
     */
    @Override
    public void afterPropertiesSet() {
        provider.ifAvailable(customizer -> customizer.customize(this));
    }
}
