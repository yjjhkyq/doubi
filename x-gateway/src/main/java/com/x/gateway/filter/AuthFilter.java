package com.x.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.x.core.constant.Constants;
import com.x.core.web.api.R;
import com.x.core.web.api.ResultCode;
import com.x.provider.api.customer.service.CustomerRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class AuthFilter implements GlobalFilter, Ordered
{
    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private final CustomerRpcService customerRpcService;

    public AuthFilter(@Lazy CustomerRpcService customerRpcService){
        this.customerRpcService = customerRpcService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String token = Optional.ofNullable(header).orElse(Constants.BEAR).substring(Constants.BEAR.length());
        final R<Long> authorizeResult = customerRpcService.authorize(token, path);
        if (authorizeResult.isOk()){
            ServerHttpRequest.Builder builder = request.mutate();
            builder.header(Constants.HTTP_HEADER_CUSTOMER_ID, authorizeResult.getData().toString());
            return chain.filter(exchange.mutate().request(builder.build()).build());
        } else{
            ServerHttpResponse response = exchange.getResponse();
            return exchange.getResponse().writeWith(
                    Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(R.build(ResultCode.UNAUTHORIZED)))));
        }
    }

    @Override
    public int getOrder()
    {
        return -200;
    }
}