package com.trail.Cadastro.model.dto.request;

import jakarta.validation.constraints.Email;

/**
 * Atualizacao parcial: os campos sao opcionais. Um campo nulo significa
 * "manter o valor atual" — por isso nao ha @NotBlank aqui. Quando o email
 * vem preenchido, porem, precisa ser um email valido (@Email aceita null).
 */
public record UsuarioUpdateRequest(
        String nome,
        @Email String email
) {
}
