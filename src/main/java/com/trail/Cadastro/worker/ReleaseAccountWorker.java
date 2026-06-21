package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.UserService;
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
public class ReleaseAccountWorker {

    private final UserService service;

    @JobWorker(type = "liberacao-conta")
    public void releaseAccount(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando liberacao-conta");

        Map<String, Object> variables = job.getVariablesAsMap();
        String userId = (String) variables.get("usuarioId");

        service.activate(userId);

        log.info("[WORKER] Conta liberada para usuario: {}", userId);
    }
}
