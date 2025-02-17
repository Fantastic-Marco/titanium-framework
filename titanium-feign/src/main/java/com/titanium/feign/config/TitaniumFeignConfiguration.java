package com.titanium.feign.config;

import com.titanium.feign.client.TitaniumFeignBlockingLoadBalancerClient;
import com.titanium.feign.client.TitaniumRetryableFeignBlockingLoadBalancerClient;
import com.titanium.feign.constants.TitaniumFeignContants;
import com.titanium.feign.interceptor.FeignLogInterceptor;
import com.titanium.feign.interceptor.TitaniumUserRequestInterceptor;
import feign.Client;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.LoadBalancerFeignRequestTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumFeignProperties.class)
public class TitaniumFeignConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = TitaniumFeignContants.PROP_PREFIX, value = "enable", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor requestInterceptor(TitaniumFeignProperties properties) {
        log.info("added titanium user request interceptor");
        return new TitaniumUserRequestInterceptor(properties);
    }

    @Bean
    @ConditionalOnProperty(prefix = TitaniumFeignContants.PROP_PREFIX, value = "log", havingValue = "true", matchIfMissing = false)
    @ConditionalOnBean({LoadBalancerClient.class, LoadBalancedRetryFactory.class, LoadBalancerClientFactory.class})
    TitaniumRetryableFeignBlockingLoadBalancerClient getRetryableFeignBlockingLoadBalancerClient(LoadBalancerClient loadBalancerClient, LoadBalancedRetryFactory loadBalancedRetryFactory, LoadBalancerClientFactory loadBalancerClientFactory, List<LoadBalancerFeignRequestTransformer> transformers) {
        log.info("added titanium retryable feign blocking load balancer client");
        return new TitaniumRetryableFeignBlockingLoadBalancerClient(new Client.Default(null, null), loadBalancerClient, loadBalancedRetryFactory, loadBalancerClientFactory, transformers);
    }

    @Bean
    @ConditionalOnProperty(prefix = TitaniumFeignContants.PROP_PREFIX, value = "log", havingValue = "true", matchIfMissing = false)
    @ConditionalOnBean({LoadBalancerClient.class, LoadBalancerClientFactory.class})
    TitaniumFeignBlockingLoadBalancerClient getFeignBlockingLoadBalancerClient(LoadBalancerClient loadBalancerClient,  LoadBalancerClientFactory loadBalancerClientFactory, List<LoadBalancerFeignRequestTransformer> transformers) {
        log.info("added titanium feign blocking load balancer client");
        return new TitaniumFeignBlockingLoadBalancerClient(new Client.Default(null, null), loadBalancerClient,  loadBalancerClientFactory, transformers);
    }

    @Bean
    @ConditionalOnProperty(prefix = TitaniumFeignContants.PROP_PREFIX, value = "log", havingValue = "true", matchIfMissing = false)
    FeignLogInterceptor feignLogInterceptor(){
        log.info("added titanium feign log interceptor");
        return new FeignLogInterceptor();
    }
}
