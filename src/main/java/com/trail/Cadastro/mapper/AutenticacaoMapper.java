package com.trail.Cadastro.mapper;

import com.trail.Cadastro.auth.TokenEmitido;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.dto.response.AutenticacaoResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutenticacaoMapper {

    public AutenticacaoResponse toResponse(Usuario usuario, TokenEmitido token) {
        return AutenticacaoResponse.builder()
                .usuario(UsuarioMapper.toResponse(usuario))
                .accessToken(token.valor())
                .expiresInSegundos(token.expiraEmSegundos())
                .build();
    }
}
