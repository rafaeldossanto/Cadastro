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
     * - Entra como ATIVO: o provedor (Google/Apple) ja confirmou o email, entao
     *   nao ha etapa de confirmacao por email.
     * - Senha fica nula: a autenticacao e delegada ao provedor.
     */
    public Usuario toEntitySocial(DadosUsuarioProvedor dados, String nome, Long sequencia) {
        return Usuario.builder()
                .id(UUID.randomUUID().toString())
                .nome(nome)
                .email(dados.email())
                .codigoUsuario(GenerateUtil.makeCode(nome, sequencia))
                .status(StatusCadastro.ATIVO)
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
