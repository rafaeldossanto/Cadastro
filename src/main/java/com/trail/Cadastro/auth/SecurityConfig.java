package com.trail.Cadastro.auth;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * O Cadastro emite o token da aplicacao e tambem se protege como resource server
 * (defense-in-depth). Publicos: cadastro, login social, confirmacao de email, o
 * JWKS e swagger/health. O resto exige o Bearer da app, validado pela chave
 * publica local ({@code appJwtDecoder}) — distinta dos decoders sociais.
 */
@Configuration
public class SecurityConfig {

    private final JwtDecoder appJwtDecoder;

    public SecurityConfig(@Qualifier("appJwtDecoder") JwtDecoder appJwtDecoder) {
        this.appJwtDecoder = appJwtDecoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/usuario").permitAll()
                        .requestMatchers("/auth/social", "/auth/confirmar-email", "/auth/dev-login", "/oauth2/jwks").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(appJwtDecoder)));
        return http.build();
    }
}
