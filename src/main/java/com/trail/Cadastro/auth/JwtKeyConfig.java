package com.trail.Cadastro.auth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * Chave RSA que assina o token da aplicacao e a fonte do JWKS publicado em
 * {@code /oauth2/jwks}. Os demais servicos validam os tokens buscando essa
 * chave publica (jwk-set-uri), sem segredo compartilhado.
 *
 * <p>A chave e carregada de um arquivo PEM (PKCS#8). O {@code kid} e derivado
 * do SHA-256 da chave publica, garantindo estabilidade entre restarts — ao
 * contrario da geracao em memoria, um restart nao invalida tokens em circulacao.
 *
 * <p>Em dev, usa {@code classpath:keys/dev-private-key.pem} (chave bundled no
 * repositorio, nao e um segredo real). Em producao, defina
 * {@code JWT_RSA_PRIVATE_KEY_PATH} apontando para o arquivo PEM seguro.
 */
@Configuration
@Slf4j
@EnableConfigurationProperties(JwtProperties.class)
public class JwtKeyConfig {

    private final JwtProperties jwtProperties;

    public JwtKeyConfig(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public RSAKey rsaKey() throws JOSEException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String pem = loadPem();
        RSAKey rsaKey = buildRsaKey(pem);
        log.info("Chave RSA carregada (kid={})", rsaKey.getKeyID());
        return rsaKey;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder appJwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

    // --- helpers ---------------------------------------------------------

    private String loadPem() throws IOException {
        String path = jwtProperties.getRsaPrivateKeyPath();
        if (path != null && !path.isBlank()) {
            log.info("Carregando chave RSA de arquivo: {}", path);
            return Files.readString(Path.of(path));
        }
        // Dev fallback: chave bundled no classpath (nao e um segredo real)
        log.warn("JWT_RSA_PRIVATE_KEY_PATH nao definida — usando chave de DESENVOLVIMENTO "
                + "(classpath:keys/dev-private-key.pem). Defina a variavel em producao.");
        try (InputStream is = getClass().getResourceAsStream("/keys/dev-private-key.pem")) {
            if (is == null) {
                throw new IllegalStateException(
                        "Chave RSA de dev nao encontrada em classpath:keys/dev-private-key.pem. "
                        + "Defina JWT_RSA_PRIVATE_KEY_PATH ou inclua o arquivo no classpath.");
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private RSAKey buildRsaKey(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String b64 = pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] derBytes = Base64.getDecoder().decode(b64);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) kf.generatePrivate(new PKCS8EncodedKeySpec(derBytes));
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(
                new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent()));

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(stableKid(publicKey))
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    private String stableKid(RSAPublicKey publicKey) throws NoSuchAlgorithmException {
        byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(publicKey.getEncoded());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256).substring(0, 16);
    }
}
