package com.me2u.gateway.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * The bean name must follow the pattern: {FilterName}GatewayFilterFactory
 * Usage in application.yaml:
 *   filters:
 *     - Authentication
 */
@Component
@RequiredArgsConstructor
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

  private final AuthenticationFilter authenticationFilter;

  @Override
  public GatewayFilter apply(Object config) {
    return authenticationFilter;
  }
}
