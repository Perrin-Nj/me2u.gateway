package com.me2u.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.me2u.gateway.util.JwtUtil;
import com.me2u.gateway.util.AppUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFilter implements GatewayFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private final JwtUtil jwtUtil;
  private final MessageSource messageSource;
  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    try {
      String token = extractToken(exchange);

      if (token == null || !jwtUtil.validateToken(token)) {
        return handleAuthenticationFailure(exchange);
      }

      // Token is valid, continue with the request
      return chain.filter(exchange);
    } catch (Exception ex) {
      log.error("Error when filtering JWT error {}", ex.getMessage(), ex);
      return handleAuthenticationFailure(exchange);
    }
  }

  private String extractToken(ServerWebExchange exchange) {
    String authHeader = exchange.getRequest().getHeaders().getFirst(AUTHORIZATION_HEADER);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    return authHeader.substring(BEARER_PREFIX.length());
  }

  private Mono<Void> handleAuthenticationFailure(ServerWebExchange exchange) {
    String message =
        messageSource.getMessage("auth.unauthorized", null, AppUtil.getDefaultLocale(exchange));

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setTitle("Authentication Failed");
    problemDetail.setDetail(message);

    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/problem+json");

    try {
      byte[] body = objectMapper.writeValueAsBytes(problemDetail);
      return exchange
          .getResponse()
          .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body)));
    } catch (Exception ex) {
      log.error("Auth failure {}", ex.getMessage(), ex);
      return exchange.getResponse().setComplete();
    }
  }
}
