package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvioEmailWorker {

    private final EmailService service;

    @JobWorker(type = "envio-email")
    public Map<String, Object> enviarEmail(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando envio-email");

        Map<String, Object> variables = job.getVariablesAsMap();
        String usuarioId = (String) variables.get("usuarioId");
        String email = (String) variables.get("email");

        String token = service.enviarConfirmacao(usuarioId, email);

        // Map.of nao aceita valor nulo; falha com mensagem clara se o token vier nulo.
        Objects.requireNonNull(token, "tokenConfirmacao nao pode ser nulo ao publicar variaveis do processo");

        return Map.of("tokenConfirmacao", token);
    }
}