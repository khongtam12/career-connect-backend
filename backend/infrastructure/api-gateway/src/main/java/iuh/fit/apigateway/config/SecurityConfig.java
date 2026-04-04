package iuh.fit.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

import static org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers.pathMatchers;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Value("${jwt.signer-key}")
    private String SECRET;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll())
                 .oauth2ResourceServer(oauth2 ->
                 oauth2.jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
                 )
                .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        SecretKeySpec key = new SecretKeySpec(SECRET.getBytes(), "HmacSHA512");
        return NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}