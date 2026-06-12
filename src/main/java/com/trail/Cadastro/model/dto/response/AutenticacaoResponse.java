package com.trail.Cadastro.model.dto.response;

import lombok.Builder;

/**
 * Resposta do login: o usuario autenticado e o access token da aplicacao que o
 * app deve enviar no header Authorization das proximas chamadas.
 */
@Builder
public record AutenticacaoResponse(
        UsuarioDTO usuario,
        String accessToken,
        long expiresInSegundos
) {
}
