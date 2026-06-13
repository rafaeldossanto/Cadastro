package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.TokenEmitido;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.mapper.AutenticacaoMapper;
import com.trail.Cadastro.mapper.UsuarioMapper;
import com.trail.Cadastro.model.dto.response.AutenticacaoResponse;
import com.trail.Cadastro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;

/**
 * Atalho de login para desenvolvimento: cria o usuario na primeira vez (ou o
 * recupera pelo email) e emite o token da app — sem provedor social nem senha.
 * Existe apenas no profile dev; em prod o bean nao e carregado.
 */
@Service
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DevAuthService {

    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    @Transactional
    public AutenticacaoResponse login(String email, String nome) {
        log.info("Dev login para {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email);
        if (isNull(usuario)) {
            Long sequencia = usuarioRepository.proximaSequencia();
            usuario = usuarioRepository.save(UsuarioMapper.toEntityDev(email, nome, sequencia));
            log.info("Usuario dev criado: {} ({})", usuario.getCodigoUsuario(), usuario.getId());
        }

        TokenEmitido token = tokenService.emitir(usuario);
        return AutenticacaoMapper.toResponse(usuario, token);
    }
}
