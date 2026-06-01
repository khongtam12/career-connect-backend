package iuh.fit.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.WebFilter;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
        @Value("${jwt.signer-key}")
        private String SECRET;

        @Bean
        @Order(1)
        public SecurityWebFilterChain publicFilterChain(ServerHttpSecurity http) {
                return http
                                .securityMatcher(pathMatchers(
                                                "/api/v1/user/auth/login",
                                                "/api/v1/user/auth/register",
                                                "/api/v1/user/auth/send-otp",
                                                "/api/v1/user/auth/verify-otp",
                                                "/api/v1/user/auth/outbound/authentication",
                                                "/api/v1/user/auth/forgot-password",
                                                "/api/v1/user/auth/reset-password",
                                                "/api/v1/user/auth/refresh",
                                                "/api/v1/job/search",
                                                "/api/v1/job/filters",
                                                "/api/v1/job/stats",
                                                "/api/v1/package/payments/vnpay-callback",
                                                "/fallback/**",
                                                "/eureka/**"))
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                                .build();
        }

        @Bean
        @Order(2)
        public SecurityWebFilterChain protectedFilterChain(ServerHttpSecurity http) {
                return http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .authorizeExchange(ex -> ex
                                                .pathMatchers("/api/v1/job/health").permitAll()
                                                .pathMatchers("/api/v1/job/chat/stream").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/v1/job/*").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/v1/package/**").permitAll()
                                                .pathMatchers(HttpMethod.GET, "/api/v1/company/*").permitAll()
                                                .pathMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/company/marketing-entitlements/company/**")
                                                .permitAll()
                                                .pathMatchers(
                                                                HttpMethod.GET,
                                                                "/api/v1/company/marketing-entitlements/featured-companies")
                                                .permitAll()
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers(
                                                                "/api/v1/user/admin/**",
                                                                "/api/v1/job/admin/**",
                                                                "/api/v1/company/pending-approvals",
                                                                "/api/v1/company/approval")
                                                .hasAuthority("SCOPE_ADMIN")
                                                .pathMatchers(
                                                                "/api/v1/company/verification",
                                                                "/api/v1/company/save",
                                                                "/api/v1/company/upload/presigned-url",
                                                                "/api/v1/package/payments/**",
                                                                "/api/v1/job/employer/**")
                                                .hasAuthority("SCOPE_EMPLOYER")
                                                .pathMatchers(HttpMethod.PUT, "/api/v1/package/**")
                                                .hasAuthority("SCOPE_ADMIN")
                                                .pathMatchers("/api/v1/job/**")
                                                .hasAnyAuthority("SCOPE_EMPLOYER", "SCOPE_CANDIDATE")
                                                .pathMatchers("/api/v1/apply/employer/**")
                                                .hasAuthority("SCOPE_EMPLOYER")
                                                .pathMatchers("/api/v1/apply/**")
                                                .hasAnyAuthority("SCOPE_EMPLOYER", "SCOPE_CANDIDATE")
                                                .pathMatchers("/api/v1/cvs/upload-avatar")
                                                .hasAnyAuthority("SCOPE_CANDIDATE", "SCOPE_ADMIN")
                                                .pathMatchers("/api/v1/cvs/**")
                                                .hasAuthority("SCOPE_CANDIDATE")
                                                .pathMatchers("/api/v1/user/auth/me", "/api/v1/storage/**")
                                                .hasAnyAuthority("SCOPE_EMPLOYER", "SCOPE_CANDIDATE", "SCOPE_ADMIN")
                                                .pathMatchers("/api/v1/notifications/chat/**")
                                                .hasAnyAuthority("SCOPE_EMPLOYER", "SCOPE_CANDIDATE")
                                                .pathMatchers("/api/v1/notifications/**")
                                                .hasAnyAuthority("SCOPE_EMPLOYER", "SCOPE_CANDIDATE", "SCOPE_ADMIN")
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder())))
                                .addFilterBefore(cookieToAuthFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                                .addFilterBefore(userHeaderFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                                .build();
        }

        @Bean
        public WebFilter cookieToAuthFilter() {
                return (exchange, chain) -> {
                        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("access_token");

                        if (cookie != null && !cookie.getValue().isBlank()) {
                                String token = cookie.getValue();
                                ServerHttpRequest mutatedRequest = exchange.getRequest()
                                                .mutate()
                                                .header("Authorization", "Bearer " + token)
                                                .build();

                                return chain.filter(exchange.mutate().request(mutatedRequest).build());
                        }

                        return chain.filter(exchange);
                };
        }

        @Bean
        public WebFilter userHeaderFilter() {
                return (exchange, chain) -> exchange.getPrincipal()
                                .flatMap(principal -> {
                                        String userId = principal.getName();
                                        ServerHttpRequest request = exchange.getRequest()
                                                        .mutate()
                                                        .header("X-User-Id", userId)
                                                        .build();

                                        return chain.filter(exchange.mutate().request(request).build());
                                })
                                .switchIfEmpty(chain.filter(exchange));
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();

                config.setAllowedOriginPatterns(List.of(
                                "http://localhost:5173",
                                "http://localhost:5174",
                                "http://localhost:3000",
                                "https://career-connect-frontend-zeta.vercel.app",
                                "https://career-connect-frontend-git-deloy-test-khng-tams-projects.vercel.app",
                                "https://*.vercel.app"));
                config.setAllowedMethods(List.of("*"));
                config.setAllowedHeaders(List.of("*"));
                config.setExposedHeaders(List.of("Set-Cookie", "Authorization"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        public ReactiveJwtDecoder jwtDecoder() {
                SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA512");
                return NimbusReactiveJwtDecoder.withSecretKey(key)
                                .macAlgorithm(MacAlgorithm.HS512)
                                .build();
        }
}
