package com.trail.Cadastro.model.dto.request;

import com.trail.Cadastro.model.enums.StatusCadastro;

import java.time.LocalDateTime;

public record UsuarioCreateRequest(
        String nome,
        String email,
        String senha,
        String codigoUsuario,
        StatusCadastro status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
