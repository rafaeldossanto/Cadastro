package com.trail.Cadastro.auth;

import com.trail.Cadastro.model.enums.ProvedorAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/**
 * Verifica ID tokens da Apple (Sign in with Apple). Diferente do Google, a
 * Apple geralmente NAO inclui o nome no token (o nome so vem no primeiro login,
 * fora do JWT) — por isso o nome pode vir nulo aqui, e o service trata isso
 * gerando um nome a partir do email quando necessario.
 */
@Component
@Slf4j
public class AppleTokenVerificador implements VerificadorTokenSocial {

    private final NimbusJwtDecoder appleJwtDecoder;

    public AppleTokenVerificador(@Qualifier("appleJwtDecoder") NimbusJwtDecoder appleJwtDecoder) {
        this.appleJwtDecoder = appleJwtDecoder;
    }

    @Override
    public ProvedorAuth provedor() {
        return ProvedorAuth.APPLE;
    }

    @Override
    public DadosUsuarioProvedor verificar(String idToken) {
        try {
            Jwt jwt = appleJwtDecoder.decode(idToken);
            return new DadosUsuarioProvedor(
                    jwt.getSubject(),
                    jwt.getClaimAsString("email"),
                    jwt.getClaimAsString("name"));
        } catch (JwtException e) {
            log.warn("ID token da Apple invalido: {}", e.getMessage());
            throw new IllegalArgumentException("Token da Apple invalido");
        }
    }
}
