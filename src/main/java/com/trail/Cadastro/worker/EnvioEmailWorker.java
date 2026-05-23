package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusConfirmacao;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvioEmailWorker {

    private final UsuarioRepository usuarioRepository;
    private final ConfirmacaoEmailRepository repository;

    @JobWorker(type = "envio-email")
    public Map<String, Object> enviarEmail(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando envio-email");

        Map<String, Object> variables = job.getVariablesAsMap();
        String usuarioId = (String) variables.get("usuarioId");
        String email = (String) variables.get("email");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + usuarioId));

        String token = UUID.randomUUID().toString();

        ConfirmacaoEmail confirmacao = ConfirmacaoEmail.builder()
                .id(UUID.randomUUID().toString())
                .usuario(usuario)
                .token(token)
                .status(StatusConfirmacao.PENDENTE)
                .expiraEm(LocalDateTime.now().plusMinutes(10))
                .build();

        repository.save(confirmacao);

        // TODO: integrar com servico de email (SendGrid, SES, etc)
        log.info("[WORKER] Email de confirmacao enviado para: {} | token: {}", email, token);

        return Map.of("tokenConfirmacao", token);
    }
}