package com.me2u.gateway.controller.advice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayControllerAdvice {

  private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

  @ExceptionHandler(ResponseStatusException.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleResponseStatusException(
      ResponseStatusException ex, ServerWebExchange exchange) {
    return buildErrorResponse(ex, ex.getReason(), exchange);
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleValidationError(
      WebExchangeBindException ex, ServerWebExchange exchange) {
    String message =
        ex.getAllErrors().isEmpty()
            ? "Validation failed"
            : ex.getAllErrors().getFirst().getDefaultMessage();
    return buildErrorResponse(
        new ResponseStatusException(HttpStatus.BAD_REQUEST), message, exchange);
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(
      Exception ex, ServerWebExchange exchange) {
    return buildErrorResponse(
        new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR),
        String.format("Internal server error --> %s", ex.getMessage()),
        exchange);
  }

  private Mono<ResponseEntity<Map<String, Object>>> buildErrorResponse(
      ResponseStatusException statusException, String message, ServerWebExchange exchange) {

    String correlationId = getOrCreateCorrelationId(exchange);

    Map<String, Object> errorAttributes = new LinkedHashMap<>();
    errorAttributes.put("timestamp", Instant.now().toString());
    errorAttributes.put("status", statusException.getStatusCode());
    errorAttributes.put("error", statusException.getReason());
    errorAttributes.put("message", message);
    errorAttributes.put("path", exchange.getRequest().getPath().value());
    errorAttributes.put("correlationId", correlationId);

    return Mono.just(ResponseEntity.status(statusException.getStatusCode()).body(errorAttributes));
  }

  private String getOrCreateCorrelationId(ServerWebExchange exchange) {
    List<String> headers = exchange.getRequest().getHeaders().getOrEmpty(CORRELATION_ID_HEADER);
    if (!headers.isEmpty() && headers.getFirst() != null && !headers.getFirst().isBlank()) {
      return headers.getFirst();
    }
    String generated = UUID.randomUUID().toString();
    // Optionally propagate downstream:
    exchange.getResponse().getHeaders().add(CORRELATION_ID_HEADER, generated);
    return generated;
  }
}
