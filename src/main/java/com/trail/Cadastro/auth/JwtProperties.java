package com.trail.Cadastro.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Parametros do token de sessao emitido pela aplicacao (distinto do ID token
 * social dos provedores). O issuer identifica o Cadastro como emissor e e
 * validado pelos resource servers; o ttl define a validade do access token.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String issuer = "http://localhost:8080";

    private long ttlSegundos = 7200;
}
