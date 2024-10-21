package com.titanium.feign.config;

import com.titanium.feign.client.TitaniumFeignClient;
import com.titanium.feign.interceptor.TitaniumUserRequestInterceptor;
import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Slf4j
@Configuration
@EnableConfigurationProperties(TitaniumFeignProperties.class)
public class TitaniumFeignConfiguration {

    @Bean
    @ConditionalOnProperty(name = "titanium.feign.enable", havingValue = "true", matchIfMissing = false)
    public RequestInterceptor requestInterceptor(TitaniumFeignProperties properties) {
        log.info("added titanium user request interceptor");
        return new TitaniumUserRequestInterceptor(properties);
    }

    @Bean
    @ConditionalOnProperty(name = "titanium.feign.log", havingValue = "true", matchIfMissing = false)
    TitaniumFeignClient getClient() throws NoSuchAlgorithmException, KeyManagementException {
//        // 忽略SSL校验
//        SSLContext ctx = SSLContext.getInstance("SSL");
//        X509TrustManager tm = new X509TrustManager() {
//            @Override
//            public void checkClientTrusted(X509Certificate[] chain, String authType) {
//            }
//
//            @Override
//            public void checkServerTrusted(X509Certificate[] chain, String authType) {
//            }
//
//            @Override
//            public X509Certificate[] getAcceptedIssuers() {
//                return null;
//            }
//        };
//        ctx.init(null, new TrustManager[]{tm}, null);
//        return new TitaniumFeignClient(ctx.getSocketFactory(), (hostname, sslSession) -> true);
        return new TitaniumFeignClient(null,null);
    }
}
