package iuh.fit.apigateway.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ApiRateLimitConfig {

    private static final Map<String, Bandwidth> API_LIMITS = new HashMap<>();

    static {
        // 🔍 PUBLIC APIs
        API_LIMITS.put("job-search",
                Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1))));

        API_LIMITS.put("job-stats",
                Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1))));

        // 🧑‍💼 EMPLOYER APIs
        API_LIMITS.put("job-create",
                Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))));

        API_LIMITS.put("job-update",
                Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1))));
    }

    public static Bandwidth getLimit(String apiGroup) {
        return API_LIMITS.get(apiGroup);
    }
}