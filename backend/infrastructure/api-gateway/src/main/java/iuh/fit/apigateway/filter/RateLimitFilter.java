package iuh.fit.apigateway.filter;

import io.github.bucket4j.Bucket;
import iuh.fit.apigateway.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final RateLimiterService rateLimiterService;

    @Value("${rate-limiter.excluded-paths:/api/v1/user/auth/login,/api/v1/user/auth/register}")
    private List<String> excludedPaths;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String apiGroup = resolveApiGroup(path, method);
        // Public endpoints: bỏ qua rate limiting
        if (isExcludedPath(path)) {
            log.debug("Skip rate limiting for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        // Protected endpoints: áp dụng rate limiting
        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(auth -> handleRequest(exchange, chain, auth, apiGroup))
                .switchIfEmpty(Mono.defer(() -> handleRequest(exchange, chain, null, apiGroup)));   }

    private Mono<Void> handleRequest(ServerWebExchange exchange,
                                     GatewayFilterChain chain,
                                     Authentication auth, String apiGroup) {
        String key = generateKey(exchange, auth);
        String ip = extractClientIp(exchange);
        // Sử dụng Mono.fromCallable nhưng cần subscribe đúng cách
        return Mono.defer(() -> {
            try {
                Bucket bucket = rateLimiterService.resolveBucket(key, apiGroup, auth);

                var probe = bucket.tryConsumeAndReturnRemaining(1);

                if (probe.isConsumed()) {

                    exchange.getResponse().getHeaders().add(
                            "X-RateLimit-Remaining",
                            String.valueOf(probe.getRemainingTokens())
                    );

                    return chain.filter(exchange);
                }

// ❌ bị limit
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

                exchange.getResponse().getHeaders().add(
                        "Retry-After",
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000)
                );

                return exchange.getResponse().setComplete();
            } catch (Exception e) {
                log.error("Rate limit error for key: {}", key, e);
                // Fallback: cho phép request đi qua
                return chain.filter(exchange);
            }
        });
    }

    private String generateKey(ServerWebExchange exchange, Authentication auth) {

        String path = exchange.getRequest().getURI().getPath();
        String method = exchange.getRequest().getMethod().name();
        String apiGroup = resolveApiGroup(path, method);

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) {

            return "user:" + auth.getName() + ":" + apiGroup + ":" + method;
        }

        String ip = extractClientIp(exchange);

        return "ip:" + ip + ":" + apiGroup + ":" + method;
    }

    private String extractClientIp(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        // Ưu tiên X-Forwarded-For (khi có proxy)
        String forwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        // Fallback to X-Real-IP
        String realIp = request.getHeaders().getFirst("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp;
        }

        // Cuối cùng là remote address
        return request.getRemoteAddress() != null ?
                request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }

    private boolean isExcludedPath(String path) {
        return excludedPaths.stream().anyMatch(path::startsWith);
    }

    private void addRateLimitHeader(ServerWebExchange exchange, Bucket bucket) {
        try {
            exchange.getResponse().getHeaders().add(
                    "X-RateLimit-Remaining",
                    String.valueOf(bucket.getAvailableTokens())
            );
        } catch (Exception e) {
            log.debug("Failed to add rate limit header: {}", e.getMessage());
        }
    }

    @Override
    public int getOrder() {
        return -2; // Chạy sau authentication filter
    }

    private String resolveApiGroup(String path, String method) {

        if (path.startsWith("/api/v1/job/search")) return "job-search";
        if (path.startsWith("/api/v1/job/stats")) return "job-stats";

        if (path.startsWith("/api/v1/job/employer/create")) return "job-create";
        if (path.startsWith("/api/v1/job/employer") && method.equals("PUT"))
            return "job-update";

        return "default";
    }
}


