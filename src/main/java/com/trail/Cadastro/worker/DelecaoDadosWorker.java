package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.TermosAceiteRepository;
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
public class DelecaoDadosWorker {

    private final UsuarioRepository usuarioRepository;
    private final ConfirmacaoEmailRepository confirmacaoEmailRepository;
    private final TermosAceiteRepository termosAceiteRepository;

    @JobWorker(type = "delecao-dados")
    public void deletarDados(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando delecao-dados por timeout");

        Map<String, Object> variables = job.getVariablesAsMap();
        String usuarioId = (String) variables.get("usuarioId");


        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado: " + usuarioId));

        if (StatusCadastro.PENDENTE.equals(usuario.getStatus())) {
            confirmacaoEmailRepository.deleteByUsuarioId(usuarioId);
            termosAceiteRepository.deleteByUsuarioId(usuarioId);
            usuarioRepository.delete(usuario);
            log.info("[WORKER] Dados deletados por timeout para usuario: {}", usuarioId);
        } else {
            log.info("[WORKER] Usuario ja ativo, delecao ignorada: {}", usuarioId);
        }
    }
}