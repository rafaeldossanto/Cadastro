package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.DadosUsuarioProvedor;
import com.trail.Cadastro.auth.VerificadorTokenSocial;
import com.trail.Cadastro.entity.ContaVinculada;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.mapper.UsuarioMapper;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.ProvedorAuth;
import com.trail.Cadastro.repository.ContaVinculadaRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Slf4j
public class AutenticacaoSocialService {

    private final UsuarioRepository usuarioRepository;
    private final ContaVinculadaRepository contaVinculadaRepository;
    private final Map<ProvedorAuth, VerificadorTokenSocial> verificadores;

    public AutenticacaoSocialService(UsuarioRepository usuarioRepository,
                                     ContaVinculadaRepository contaVinculadaRepository,
                                     List<VerificadorTokenSocial> verificadoresDisponiveis) {
        this.usuarioRepository = usuarioRepository;
        this.contaVinculadaRepository = contaVinculadaRepository;
        this.verificadores = new EnumMap<>(ProvedorAuth.class);
        verificadoresDisponiveis.forEach(v -> this.verificadores.put(v.provedor(), v));
    }

    @Transactional
    public UsuarioDTO autenticar(ProvedorAuth provedor, String idToken) {
        DadosUsuarioProvedor dados = verificar(provedor, idToken);
        log.info("Login social {} para subject {}", provedor, dados.subject());

        Usuario usuario = contaVinculadaRepository
                .findByProvedorAndProvedorUsuarioId(provedor, dados.subject())
                .map(ContaVinculada::getUsuario)
                .orElseGet(() -> resolverPorEmailOuCriar(provedor, dados));

        return UsuarioMapper.toResponse(usuario);
    }

    private DadosUsuarioProvedor verificar(ProvedorAuth provedor, String idToken) {
        VerificadorTokenSocial verificador = verificadores.get(provedor);
        if (isNull(verificador)) {
            throw new IllegalArgumentException("Provedor de login nao suportado: " + provedor);
        }
        return verificador.verificar(idToken);
    }

    private Usuario resolverPorEmailOuCriar(ProvedorAuth provedor, DadosUsuarioProvedor dados) {
        Usuario existente = nonNull(dados.email()) ? usuarioRepository.findByEmail(dados.email()) : null;

        Usuario usuario = nonNull(existente) ? existente : criarUsuarioSocial(dados);
        vincular(usuario, provedor, dados);
        return usuario;
    }

    private Usuario criarUsuarioSocial(DadosUsuarioProvedor dados) {
        Long sequencia = usuarioRepository.proximaSequencia();
        Usuario usuario = UsuarioMapper.toEntitySocial(dados, nomeOuPadrao(dados), sequencia);
        Usuario salvo = usuarioRepository.save(usuario);
        log.info("Usuario social criado: {} ({})", salvo.getCodigoUsuario(), salvo.getId());
        return salvo;
    }

    private void vincular(Usuario usuario, ProvedorAuth provedor, DadosUsuarioProvedor dados) {
        ContaVinculada vinculo = ContaVinculada.builder()
                .id(UUID.randomUUID().toString())
                .usuario(usuario)
                .provedor(provedor)
                .provedorUsuarioId(dados.subject())
                .email(dados.email())
                .vinculadoEm(LocalDateTime.now())
                .build();
        contaVinculadaRepository.save(vinculo);
        log.info("Conta {} vinculada ao usuario {}", provedor, usuario.getId());
    }

    private String nomeOuPadrao(DadosUsuarioProvedor dados) {
        if (nonNull(dados.nome()) && !dados.nome().isBlank()) {
            return dados.nome();
        }
        if (nonNull(dados.email()) && dados.email().contains("@")) {
            return dados.email().substring(0, dados.email().indexOf("@"));
        }
        return "usuario";
    }
}
