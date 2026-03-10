package com.me2u.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final LoggingFilter loggingFilter;

    @Override
    public GatewayFilter apply(Object config) {
        return loggingFilter;
    }
}
