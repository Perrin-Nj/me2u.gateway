package com.me2u.gateway.controller;

import com.me2u.gateway.controller.dto.ApiErrorResponseDto;
import com.me2u.gateway.controller.utils.Utils;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
  public Mono<ResponseEntity<ApiErrorResponseDto>> authFallback(ServerWebExchange exchange) {

    String message =
        messageSource.getMessage(
            "auth.service.down",
            new Object[] {"Authentification"},
            Utils.getDefaultLocale(exchange));

    ApiErrorResponseDto body =
        new ApiErrorResponseDto(HttpStatus.SERVICE_UNAVAILABLE.value(), message);
    return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
  }
}
