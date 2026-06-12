package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.JwtProperties;
import com.trail.Cadastro.auth.TokenEmitido;
import com.trail.Cadastro.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Emite o access token da aplicacao apos a autenticacao. O subject e o id
 * interno (UUID); o codigoUsuario vai como claim por ser o identificador
 * publico usado nas relacoes (amizades, sessoes).
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtProperties properties;

    public TokenEmitido emitir(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(properties.getIssuer())
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(properties.getTtlSegundos()))
                .subject(usuario.getId())
                .claim("codigoUsuario", usuario.getCodigoUsuario())
                .claim("email", usuario.getEmail())
                .claim("nome", usuario.getNome())
                .build();

        String valor = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new TokenEmitido(valor, properties.getTtlSegundos());
    }
}
