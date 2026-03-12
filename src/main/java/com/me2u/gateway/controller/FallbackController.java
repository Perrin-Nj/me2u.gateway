package com.me2u.gateway.controller;

import com.me2u.gateway.util.AppUtil;
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
@RequestMapping("/fallback")
public class FallbackController {

  private final MessageSource messageSource;

  public FallbackController(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @GetMapping("/auth")
  @PostMapping("/auth")
  public Mono<ResponseEntity<ProblemDetail>> authFallback(ServerWebExchange exchange) {
    String message =
        messageSource.getMessage(
            "auth.service.down",
            new Object[] {"Authentication"},
            AppUtil.getDefaultLocale(exchange));

    ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
    problemDetail.setTitle("Service Unavailable");
    problemDetail.setDetail(message);

    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(problemDetail));
  }
}
