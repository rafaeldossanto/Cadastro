package com.trail.Cadastro.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Criacao de usuario: todos os campos sao obrigatorios, pois e a primeira
 * entrada do dado no sistema.
 */
public record UsuarioCreateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}
