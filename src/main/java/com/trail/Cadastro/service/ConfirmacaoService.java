package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.TermosAceite;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusConfirmacao;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.TermosAceiteRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmacaoService {

    private final ConfirmacaoEmailRepository confirmacaoRepository;
    private final TermosAceiteRepository termosRepository;
    private final UsuarioRepository usuarioRepository;
    private final ZeebeClient zeebeClient;

    public void confirmarEmail(String token) {
        log.info("Confirmando email com token: {}", token);

        ConfirmacaoEmail confirmacao = confirmacaoRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token invalido"));

        if (confirmacao.getStatus() == StatusConfirmacao.CONFIRMADO) {
            throw new IllegalArgumentException("Email ja confirmado");
        }

        if (confirmacao.getExpiraEm().isBefore(LocalDateTime.now())) {
            confirmacao.setStatus(StatusConfirmacao.EXPIRADO);
            confirmacaoRepository.save(confirmacao);
            throw new IllegalArgumentException("Token expirado");
        }

        confirmacao.setStatus(StatusConfirmacao.CONFIRMADO);
        confirmacao.setDataConfirmacao(LocalDateTime.now());
        confirmacaoRepository.save(confirmacao);

        zeebeClient.newPublishMessageCommand()
                .messageName("email-confirmado")
                .correlationKey(confirmacao.getUsuario().getId())
                .send()
                .join();

        log.info("Email confirmado para usuario: {}", confirmacao.getUsuario().getId());
    }

    public void aceitarTermos(String usuarioId, String versaoTermos) {
        log.info("Aceitando termos para usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        TermosAceite termos = TermosAceite.builder()
                .id(UUID.randomUUID().toString())
                .usuario(usuario)
                .aceito(true)
                .dataAceite(LocalDateTime.now())
                .build();

        termosRepository.save(termos);

        zeebeClient.newPublishMessageCommand()
                .messageName("termos-aceitos")
                .correlationKey(usuarioId)
                .send()
                .join();

        log.info("Termos aceitos para usuario: {}", usuarioId);
    }
}
