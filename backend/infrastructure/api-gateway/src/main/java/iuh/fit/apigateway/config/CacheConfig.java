package iuh.fit.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary; // Quan trọng
import org.springframework.cache.CacheManager; // Đây là CacheManager của Spring

// Import CacheManager của JCache (JSR-107)
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    // Tên cache này sẽ được dùng trong application.yml
    public static final String RATE_LIMIT_CACHE = "rate-limit-buckets";

    // --- CẤU HÌNH CHO BUCKET4J (JCACHE) ---

    @org.springframework.beans.factory.annotation.Value("${REDIS_HOST:redis}")
    private String redisHost;

    @Bean
    public Config redissonConfig() {
        Config config = new Config();
        // Cấu hình kết nối Redis
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":6379");
        // .setPassword("your-password");
        return config;
    }

    /**
     * Bean này dành RIÊNG cho Bucket4j.
     * Bucket4j starter sẽ tự động tìm Bean có kiểu javax.cache.CacheManager.
     */
    @Bean
    public javax.cache.CacheManager jCacheManagerForBucket4j(Config redissonConfig) {

        CachingProvider provider = Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager();

        // Tạo cấu hình JCache từ cấu hình Redisson
        javax.cache.configuration.Configuration<Object, Object> jcacheConfig =
                RedissonConfiguration.fromConfig(redissonConfig);

        // **Mấu chốt "chống lỗi vặt"**:
        // Chủ động tạo cache trước. Nếu không, filter đầu tiên truy cập
        // có thể gặp lỗi "cache not found" trong môi trường đa luồng.
        if (cacheManager.getCache(RATE_LIMIT_CACHE) == null) {
            cacheManager.createCache(RATE_LIMIT_CACHE, jcacheConfig);
            log.info("✅ JCache '{}' for Bucket4j initialized.", RATE_LIMIT_CACHE);
        }

        return cacheManager;
    }

    /*
    // --- (TÙY CHỌN) NẾU BẠN CŨNG DÙNG SPRING CACHE (@Cacheable) ---
    // Nếu bạn muốn dùng RedisCacheManager cho @Cacheable
    // Hãy định nghĩa nó và đánh dấu @Primary.

    @Bean
    @Primary // Báo Spring dùng cái này cho @Cacheable
    public CacheManager springCacheManager(RedisConnectionFactory factory) {
        // ... (Tạo RedisCacheManager của Spring) ...
        log.info("✅ Primary Spring Cache Manager (RedisCacheManager) initialized.");
        return RedisCacheManager.builder(factory).build();
    }
    */
}
