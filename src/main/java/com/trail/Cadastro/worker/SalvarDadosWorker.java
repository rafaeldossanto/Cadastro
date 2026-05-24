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

        log.info("[WORKER] Usuario salvo com codigo: {}", usuario.codigoUsuario());

        return Map.of(
                "usuarioId", usuario.codigoUsuario(),
                "email", usuario.email()
        );
    }
}
