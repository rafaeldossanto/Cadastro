package com.trail.Cadastro.model.dto.response;

import com.trail.Cadastro.model.enums.StatusCadastro;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UsuarioDTO(
        String nome,
        String email,
        String senha,
        String codigoUsuario,
        StatusCadastro status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
}
