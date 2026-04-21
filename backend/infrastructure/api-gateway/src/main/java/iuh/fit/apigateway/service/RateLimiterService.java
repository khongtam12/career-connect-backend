package iuh.fit.apigateway.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import iuh.fit.apigateway.config.CacheConfig;
import lombok.extern.slf4j.Slf4j;
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

    private static final Bandwidth ANONYMOUS_LIMIT =
            Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
    private static final Bandwidth FREE_LIMIT =
            Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
    private static final Bandwidth EMPLOYER_LIMIT =
            Bandwidth.classic(500, Refill.greedy(500, Duration.ofMinutes(1)));
    private static final Bandwidth ADMIN_LIMIT =
            Bandwidth.classic(1000, Refill.greedy(1000, Duration.ofMinutes(1)));

    public RateLimiterService(CacheManager jCacheManager) {
        Cache<String, byte[]> cache = jCacheManager.getCache(CacheConfig.RATE_LIMIT_CACHE);
        this.proxyManager = new JCacheProxyManager<>(cache);
        log.info("RateLimiterService initialized");
    }

    public Bucket resolveBucket(String key, Authentication auth) {
        if (key == null || key.isBlank()) {
            key = "fallback:" + System.currentTimeMillis();
            log.warn("Using fallback key: {}", key);
        }

        Bandwidth bandwidth = determineBandwidth(auth);

        Supplier<BucketConfiguration> configSupplier = () ->
                BucketConfiguration.builder()
                        .addLimit(bandwidth)
                        .build();

        return proxyManager.builder().build(key, configSupplier);
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