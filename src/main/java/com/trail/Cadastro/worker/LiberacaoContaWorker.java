package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.UsuarioRepository;
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
public class LiberacaoContaWorker {

    private final UsuarioRepository usuarioRepository;

    @JobWorker(type = "liberacao-conta")
    public void liberarConta(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando liberacao-conta");

        Map<String, Object> variables = job.getVariablesAsMap();
        String usuarioId = (String) variables.get("usuarioId");

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + usuarioId));

        usuario.setStatus(StatusCadastro.ATIVO);
        usuarioRepository.save(usuario);

        log.info("[WORKER] Conta liberada para usuario: {}", usuarioId);
    }
}