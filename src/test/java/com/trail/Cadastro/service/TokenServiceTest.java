package com.trail.Cadastro.service;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.trail.Cadastro.auth.JwtProperties;
import com.trail.Cadastro.auth.TokenEmitido;
import com.trail.Cadastro.entity.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenService")
class TokenServiceTest {

    private TokenService tokenService;
    private JwtDecoder decoder;

    @BeforeEach
    void setUp() throws Exception {
        RSAKey rsaKey = new RSAKeyGenerator(2048).keyID("test").generate();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        JwtEncoder encoder = new NimbusJwtEncoder(jwkSource);

        JwtProperties props = new JwtProperties();
        props.setIssuer("http://localhost:8080");
        props.setTtlSegundos(7200);

        tokenService = new TokenService(encoder, props);
        decoder = NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    @Test
    @DisplayName("emitir deve gerar um token assinado com os claims do usuario")
    void deveEmitirTokenComClaims() {
        Usuario usuario = Usuario.builder()
                .id("id-1")
                .nome("Rafael")
                .email("rafael@email.com")
                .codigoUsuario("rafael#1")
                .build();

        TokenEmitido emitido = tokenService.emitir(usuario);

        assertThat(emitido.expiraEmSegundos()).isEqualTo(7200);

        Jwt jwt = decoder.decode(emitido.valor());
        assertThat(jwt.getSubject()).isEqualTo("id-1");
        assertThat(jwt.getClaimAsString("codigoUsuario")).isEqualTo("rafael#1");
        assertThat(jwt.getClaimAsString("email")).isEqualTo("rafael@email.com");
        assertThat(jwt.getClaimAsString("nome")).isEqualTo("Rafael");
        assertThat(jwt.getIssuer().toString()).isEqualTo("http://localhost:8080");
        assertThat(jwt.getExpiresAt()).isNotNull();
    }
}
