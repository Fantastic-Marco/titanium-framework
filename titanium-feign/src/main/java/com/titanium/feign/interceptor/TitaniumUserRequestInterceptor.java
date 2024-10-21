package com.titanium.feign.interceptor;

import com.titanium.common.constant.WebHeaderConstants;
import com.titanium.common.user.UserContext;
import com.titanium.common.user.UserContextHolder;
import com.titanium.feign.config.TitaniumFeignProperties;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Titanium 默认的Feign拦截器
 * 会往请求中尽可能地放置用户信息以及请求头
 */
@Slf4j
public class TitaniumUserRequestInterceptor implements RequestInterceptor {
    private TitaniumFeignProperties properties;

    public TitaniumUserRequestInterceptor(TitaniumFeignProperties properties) {
        this.properties = properties;
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        UserContext userContext = UserContextHolder.get();
        //请求头放置
        putHeader(requestTemplate, WebHeaderConstants.USER_ID, userContext.getUserId());
        putHeader(requestTemplate, WebHeaderConstants.USER_NAME, userContext.getUserName());
        putHeader(requestTemplate, WebHeaderConstants.TENANT_ID, userContext.getTenantId());
        putHeader(requestTemplate, WebHeaderConstants.TENANT_USER_ID, userContext.getTenantUserId());
        putHeader(requestTemplate, WebHeaderConstants.SYSTEM_CODE, userContext.getSystemCode());
        putHeader(requestTemplate, WebHeaderConstants.AUTHORIZATION, userContext.getAuthorization());
        if (properties.isLogCurl()) {
            log.info("curl to replay:\n {}", toCurl(requestTemplate));
        }
    }

    /**
     * 往请求头中放置值
     * @param requestTemplate
     * @param headerCode
     * @param value
     */
    private void putHeader(RequestTemplate requestTemplate, String headerCode, Object value) {
        putIfNotNull(
                requestTemplate, headerCode,
                Optional.ofNullable(value)
                        .map(String::valueOf)
                        .orElse(getHeaderStringValue(requestTemplate.request(), headerCode))
        );
    }

    /**
     * 获取请求头中的字符值
     */
    private String getHeaderStringValue(Request request, String headerName) {
        Collection<String> values = request.headers().get(headerName);
        if (values == null || values.isEmpty()) {
            return null;
        } else if (values.size() > 1) {
            log.warn("请求头中存在多个{}", headerName);
        }
        return values.iterator().next();
    }

    /**
     * 获取请求头中的数字
     */
    private Long getHeaderLongValue(Request request, String headerName) {
        String value = getHeaderStringValue(request, headerName);
        if (value == null) {
            return null;
        } else {
            try {
                return Long.valueOf(value);
            } catch (NumberFormatException e) {
                log.warn("请求头中{}的值无法转换为数字", headerName);
                return null;
            }
        }
    }

    /**
     * 如果值不为空则放置到请求头中
     */
    private void putIfNotNull(RequestTemplate requestTemplate, String headerName, Object value) {
        try {
            if (value != null) {
                requestTemplate.header(headerName, String.valueOf(value));
            }
        } catch (Exception e) {
            log.warn("请求头中{}的值无法转换为字符串", headerName);
        }
    }

    public String toCurl(feign.RequestTemplate template) {
        var headers = Arrays.stream(template.headers().entrySet().toArray()).map(header -> header.toString().replace('=', ':').replace('[', ' ').replace(']', ' ')).map(h -> String.format(" --header '%s'\\\n", h)).collect(Collectors.joining());
        var httpMethod = template.method().toUpperCase(Locale.ROOT);
        var url = template.feignTarget().url() + template.url();
        var body = new String(template.body(), StandardCharsets.UTF_8);

        return String.format("curl --location --request %s '%s' \\\n%s \\\n--data-raw '%s'", httpMethod, url, headers, body);
    }
}
