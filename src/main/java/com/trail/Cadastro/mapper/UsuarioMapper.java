package com.trail.Cadastro.mapper;

import com.trail.Cadastro.auth.DadosUsuarioProvedor;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
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

    /**
     * Cria um Usuario a partir dos dados validados de um provedor social.
     * Pontos importantes:
     * - Gera o codigoUsuario (nome + sequencia) igual ao cadastro normal, para
     *   que o usuario social possa ser encontrado/adicionado como amigo.
     * - Entra como ATIVO: o provedor (Google/Apple) 