package com.me2u.gateway.controller;

import com.me2u.gateway.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fallback")
public class FallbackController {

  private final MessageSource messageSource;

  @GetMapping("/auth")
  @PostMapping("/auth")
  public Mono<ResponseEntity<ProblemDetail>> authFallback(ServerWebExchange exchange) {
    return buildFallback(exchange, "auth.service.down", "Authentication");
  }

  @GetMapping("/shipment")
  @PostMapping("/shipment")
  public Mono<ResponseEntity<ProblemDetail>> shipmentFallback(ServerWebExchange exchange) {
    return buildFallback(exchange, "shipment.service.down", "Shipment");
  }

  @GetMapping("/notification")
  @PostMapping("/notification")
  public Mono<ResponseEntity<ProblemDetail>> notificationFallback(ServerWebExchange exchange) {
    return buildFallback(exchange, "notification.service.down", "Notification");
  }


  private Mono<ResponseEntity<ProblemDetail>> buildFallback(
          ServerWebExchange exchange, String messageKey, String serviceName) {

    String message = messageSource.getMessage(
            messageKey,
            new Object[]{serviceName},
            AppUtil.getDefaultLocale(exchange));

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
    problemDetail.setTitle("Service Unavailable");
    problemDetail.setDetail(message);

    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail));
  }
}
