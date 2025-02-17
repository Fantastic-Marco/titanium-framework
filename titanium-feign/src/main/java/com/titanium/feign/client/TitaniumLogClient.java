package com.titanium.feign.client;

import feign.Request;
import feign.Response;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface TitaniumLogClient {

    default Response log(Request request, Request.Options options, BiFunction<Request, Request.Options, Response> function, Consumer<String> logConsumer) throws IOException {
        StringBuilder sb = new StringBuilder("[log started]\r\n");
        sb.append(request.httpMethod()).append(" ").append(request.url()).append("\r\n");
        CombineHeaders(sb, request.headers()); // 请求Header
        CombineBody(sb, request.body());

        long costTime = -1;
        Exception exception = null;
        BufferingFeignClientResponse response = null;
        long begin = System.currentTimeMillis();
        try {
            response = new BufferingFeignClientResponse(function.apply(request, options));
            costTime = (System.currentTimeMillis() - begin);
        } catch (Exception exp) {
            costTime = (System.currentTimeMillis() - begin);
            exception = exp;
            throw exp;
        } finally {
            sb.append("\r\nResponse cost time(ms)： ").append(costTime);
            if (response != null) {
                sb.append("  status: ").append(response.status());
                sb.append("  reason: ").append(response.response.reason());
            }
            sb.append("\r\n");
            if (response != null) {
                CombineHeaders(sb, response.headers()); // 响应Header
                sb.append("Body:\r\n").append(response.body()).append("\r\n");
            }
            if (exception != null) {
                sb.append("Exception:\r\n  ").append(exception.getMessage()).append("\r\n");
            }
            sb.append("\r\n[log ended]");
            logConsumer.accept(sb.toString());
        }
        int length = 0;
        if (response != null && response.getResponse().body() != null) {
            length = response.getResponse().body().length();
        }
        Response ret = response.getResponse().toBuilder()
                .body(response.getBody(),
                        length).build();
        response.close();

        return ret;
    }

    private static void CombineHeaders(StringBuilder sb, Map<String, Collection<String>> headers) {
        if (headers != null && !headers.isEmpty()) {
            sb.append("Headers:\r\n");
            for (Map.Entry<String, Collection<String>> ob : headers.entrySet()) {
                for (String val : ob.getValue()) {
                    sb.append("  ").append(ob.getKey()).append(": ").append(val).append("\r\n");
                }
            }
        }
    }

    private static void CombineBody(StringBuilder sb, byte[] body) {
        if (body == null || body.length <= 0)
            return;
        sb.append("Body:\r\n").append(new String(body)).append("\r\n");
    }
}
