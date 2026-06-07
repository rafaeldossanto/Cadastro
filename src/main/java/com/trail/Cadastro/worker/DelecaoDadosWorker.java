package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.UsuarioService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DelecaoDadosWorker {

    private final UsuarioService service;

    @JobWorker(type = "delecao-dados")
    public void deletarDados(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando delecao-dados por timeout");

        Map<String, Object> variables = job.getVariablesAsMap();
        String usuarioId = (String) variables.get("usuarioId");

        service.delete(usuarioId);

        log.info("[WORKER] Dados deletados por timeout para usuario: {}", usuarioId);
    }
}