package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusConfirmacao;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final UsuarioRepository repository;
    private final ConfirmacaoEmailRepository emailRepository;

    public String enviarConfirmacao(String usuarioId, String email) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));

        String token = UUID.randomUUID().toString();

        ConfirmacaoEmail confirmacao = ConfirmacaoEmail.builder()
                .usuario(usuario)
                .token(token)
                .status(StatusConfirmacao.PENDENTE)
                .expiraEm(LocalDateTime.now().plusMinutes(10))
                .build();

        emailRepository.save(confirmacao);

        // TODO: integrar com servico de email (SendGrid, SES, etc)
        log.info("Email de confirmacao enviado para: {} | token: {}", email, token);

        return token;
    }
}