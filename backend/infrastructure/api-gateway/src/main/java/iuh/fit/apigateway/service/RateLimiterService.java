package iuh.fit.apigateway.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import iuh.fit.apigateway.config.ApiRateLimitConfig;
import iuh.fit.apigateway.config.CacheConfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.time.Duration;
import java.util.Collection;
import java.util.function.Supplier;

@Slf4j
@Service
public class RateLimiterService {

    private final ProxyManager<String> proxyManager;
    @Value("${rate-limiter.version}")
    private String KEY_VERSION; // đổi khi change config
    private static final Bandwidth ANONYMOUS_LIMIT = Bandwidth.classic(60,
            Refill.intervally(60, Duration.ofMinutes(1)));
    private static final Bandwidth FREE_LIMIT = Bandwidth.classic(80, Refill.intervally(80, Duration.ofMinutes(1)));
    private static final Bandwidth EMPLOYER_LIMIT = Bandwidth.classic(300,
            Refill.intervally(300, Duration.ofMinutes(1)));
    private static final Bandwidth ADMIN_LIMIT = Bandwidth.classic(1000,
            Refill.intervally(1000, Duration.ofMinutes(1)));

    public RateLimiterService(CacheManager jCacheManager) {
        Cache<String, byte[]> cache = jCacheManager.getCache(CacheConfig.RATE_LIMIT_CACHE);
        this.proxyManager = new JCacheProxyManager<>(cache);
        log.info("RateLimiterService initialized");
    }

    public Bucket resolveBucket(String key,
            String apiGroup,
            Authentication auth) {

        String finalKey = KEY_VERSION + ":" + key;

        Supplier<BucketConfiguration> configSupplier = () -> {
            Bandwidth roleLimit = determineBandwidth(auth);
            Bandwidth apiLimit = ApiRateLimitConfig.getLimit(apiGroup);

            BucketConfiguration configuration = BucketConfiguration.builder()
                    .addLimit(roleLimit)
                    .addLimit(
                            Bandwidth.classic(50,
                                    Refill.greedy(50, Duration.ofSeconds(1))))
                    .build();

            if (apiLimit != null) {
                configuration = BucketConfiguration.builder()
                        .addLimit(roleLimit)
                        .addLimit(apiLimit)
                        .addLimit(
                                Bandwidth.classic(50,
                                        Refill.greedy(10, Duration.ofSeconds(1))))
                        .build();
            }

            return configuration;
        };

        return proxyManager.builder().build(finalKey, configSupplier);
    }

    private Bandwidth determineBandwidth(Authentication auth) {
        if (auth == null) {
            return ANONYMOUS_LIMIT;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null || authorities.isEmpty()) {
            return FREE_LIMIT;
        }

        boolean isAdmin = authorities.stream()
                .anyMatch(a -> {
                    String role = a.getAuthority();
                    return role.equals("ROLE_ADMIN")
                            || role.equals("ADMIN")
                            || role.equals("SCOPE_ADMIN");
                });

        boolean isEmployer = authorities.stream()
                .anyMatch(a -> {
                    String role = a.getAuthority();
                    return role.equals("ROLE_EMPLOYER")
                            || role.equals("EMPLOYER")
                            || role.equals("SCOPE_EMPLOYER");
                });

        if (isAdmin) {
            log.debug("Admin user: {}, limit {} req/min", auth.getName(), ADMIN_LIMIT.getCapacity());
            return ADMIN_LIMIT;
        } else if (isEmployer) {
            log.debug("Employer user: {}, limit {} req/min", auth.getName(), EMPLOYER_LIMIT.getCapacity());
            return EMPLOYER_LIMIT;
        } else {
            log.debug("Free user: {}, limit {} req/min", auth.getName(), FREE_LIMIT.getCapacity());
            return FREE_LIMIT;
        }
    }
}