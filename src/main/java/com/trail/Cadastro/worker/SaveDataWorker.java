package com.trail.Cadastro.worker;

import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.service.UserService;
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
public class SaveDataWorker {

    private final UserService service;

    @JobWorker(type = "salvar-dados")
    public Map<String, Object> saveData(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando salvar-dados");

        Map<String, Object> variables = job.getVariablesAsMap();
        UserCreateRequest request = new UserCreateRequest(
                (String) variables.get("nome"),
                (String) variables.get("email"),
                (String) variables.get("senha")
        );

        UserDTO user = service.create(request);

        log.info("[WORKER] Usuario salvo com id: {} e codigo: {}", user.id(), user.userCode());

        // Map.of nao aceita valores nulos (estoura NPE cripitco). Validamos antes
        // para falhar com uma mensagem clara caso o create nao preencha os campos.
        Objects.requireNonNull(user.id(), "usuarioId nao pode ser nulo ao publicar variaveis do processo");
        Objects.requireNonNull(user.email(), "email nao pode ser nulo ao publicar variaveis do processo");

        return Map.of(
                "usuarioId", user.id(),
                "email", user.email()
        );
    }
}
