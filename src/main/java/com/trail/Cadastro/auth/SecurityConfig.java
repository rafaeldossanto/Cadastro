package com.trail.Cadastro.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Adicionar o starter oauth2-resource-server traz o Spring Security, que por
 * padrao bloquearia todos os endpoints. Este microservico de Cadastro roda
 * ATRAS do BFF (nao e exposto direto ao publico) e nao usa o Security para
 * proteger seus proprios endpoints — a validacao de ID token social e feita
 * manualmente nos verificadores (JwtDecoder), nao pelo filtro de seguranca.
 *
 * Por isso liberamos os endpoints aqui. A protecao real do perimetro fica no
 * BFF / gateway. Quando houver necessidade de proteger rotas internas, este e
 * o lugar para configurar.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
