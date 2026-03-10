package com.me2u.gateway.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggingFilter implements GatewayFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String requestId = exchange.getRequest().getHeaders()
                .getFirst(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        final String finalRequestId = requestId;
        long startTime = Instant.now().toEpochMilli();
        String method = exchange.getRequest().getMethod().name();
        String path   = exchange.getRequest().getURI().getPath();

        log.debug("→ [{}] {} {}", finalRequestId, method, path);

        var mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header(REQUEST_ID_HEADER, finalRequestId)
                        .build())
                .build();

        mutatedExchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, finalRequestId);

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            long duration = Instant.now().toEpochMilli() - startTime;
            int  status   = mutatedExchange.getResponse().getStatusCode() != null
                    ? mutatedExchange.getResponse().getStatusCode().value() : 0;
            log.debug("← [{}] {} {} → {} ({}ms)",
                    finalRequestId, method, path, status, duration);
        }));
    }
}
