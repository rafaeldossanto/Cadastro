package com.trail.Cadastro.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record UsuarioCreateRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha
) {
}
