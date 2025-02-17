package com.titanium.feign.client;

import feign.Client;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.cloud.openfeign.loadbalancer.FeignBlockingLoadBalancerClient;
import org.springframework.cloud.openfeign.loadbalancer.LoadBalancerFeignRequestTransformer;

import java.io.IOException;
import java.util.List;

@Slf4j
public class TitaniumFeignBlockingLoadBalancerClient extends FeignBlockingLoadBalancerClient implements TitaniumLogClient {
    public TitaniumFeignBlockingLoadBalancerClient(Client delegate, LoadBalancerClient loadBalancerClient, LoadBalancerClientFactory loadBalancerClientFactory, List<LoadBalancerFeignRequestTransformer> transformers) {
        super(delegate, loadBalancerClient, loadBalancerClientFactory, transformers);
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
