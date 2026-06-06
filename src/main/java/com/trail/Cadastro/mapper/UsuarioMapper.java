package com.trail.Cadastro.mapper;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.utils.GenerateUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UsuarioMapper {

    public Usuario toEntity(UsuarioCreateRequest request, Long sequencia) {
        return Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nome(request.nome())
                .email(request.email())
                .senha(request.senha())
                .codigoUsuario(GenerateUtil.makeCode(request.nome(), sequencia))
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    public UsuarioDTO toResponse(Usuario usuario) {
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .codigoUsuario(usuario.getCodigoUsuario())
                .dataCriacao(usuario.getDataCriacao())
                .dataAtualizacao(usuario.getDataAtualizacao())
                .build();
    }
}
