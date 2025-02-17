package com.titanium.feign.client;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.LoadBalancerFeignRequestTransformer;
import org.springframework.cloud.openfeign.loadbalancer.RetryableFeignBlockingLoadBalancerClient;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public class TitaniumRetryableFeignBlockingLoadBalancerClient extends RetryableFeignBlockingLoadBalancerClient implements TitaniumLogClient {


    public TitaniumRetryableFeignBlockingLoadBalancerClient(Client delegate, LoadBalancerClient loadBalancerClient, LoadBalancedRetryFactory loadBalancedRetryFactory, LoadBalancerClientFactory loadBalancerClientFactory, List<LoadBalancerFeignRequestTransformer> transformers) {
        super(delegate, loadBalancerClient, loadBalancedRetryFactory, loadBalancerClientFactory, transformers);
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return log(
                request,
                options,
                (r, o) -> {
                    try {
                        return super.execute(request, options);
                    } catch (IOException e) {
                        throw new RuntimeException("Error executing request", e);
                    }
                },
                log::debug
        );
    }
}
