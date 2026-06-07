package com.trail.Cadastro.worker;

import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.service.UsuarioService;
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
public class SalvarDadosWorker {

    private final UsuarioService service;

    @JobWorker(type = "salvar-dados")
    public Map<String, Object> salvarDados(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando salvar-dados");

        Map<String, Object> variables = job.getVariablesAsMap();
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                (String) variables.get("nome"),
                (String) variables.get("email"),
                (String) variables.get("senha")
        );

        UsuarioDTO usuario = service.create(request);

        log.info("[WORKER] Usuario salvo com id: {} e codigo: {}", usuario.id(), usuario.codigoUsuario());

        // Map.of nao aceita valores nulos (estoura NPE cripitco). Validamos antes
        // para falhar com uma mensagem clara caso o create nao preencha os campos.
        Objects.requireNonNull(usuario.id(), "usuarioId nao pode ser nulo ao publicar variaveis do processo");
        Objects.requireNonNull(usuario.email(), "email nao pode ser nulo ao publicar variaveis do processo");

        return Map.of(
                "usuarioId", usuario.id(),
                "email", usuario.email()
        );
    }
}
