package iuh.fit.apigateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback/service")
    public Mono<ResponseEntity<Map<String, Object>>> serviceFallback(ServerWebExchange exchange) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 503);
        response.put("message", "Dich vu hien tai khong kha dung. Vui long thu lai sau giay lat.");
        response.put("error", "Service Unavailable (Circuit Breaker)");

        Throwable error = exchange.getAttribute(ServerWebExchangeUtils.CIRCUITBREAKER_EXECUTION_EXCEPTION_ATTR);
        String originalPath = exchange.getRequest().getHeaders().getFirst("X-Forwarded-Path");

        log.warn("Gateway fallback triggered for path={} cause={}",
                originalPath != null ? originalPath : exchange.getRequest().getPath(),
                error != null ? error.toString() : "response-status-trigger");

        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response));
    }
}
