package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.mapper.UsuarioMapper;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
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
public class SalvarDadosWorker {

    private final UsuarioRepository usuarioRepository;

    @JobWorker(type = "salvar-dados")
    public Map<String, Object> salvarDados(JobClient client, ActivatedJob job) {
        log.info("[WORKER] Iniciando salvar-dados");

        Map<String, Object> variables = job.getVariablesAsMap();
        String nome = (String) variables.get("nome");
        String email = (String) variables.get("email");
        String senha = (String) variables.get("senha");

        UsuarioCreateRequest request = new UsuarioCreateRequest(nome, email, senha);

        Long sequencia = usuarioRepository.proximaSequencia();
        Usuario usuario = UsuarioMapper.toEntity(request, sequencia);
        usuarioRepository.save(usuario);

        log.info("[WORKER] Usuario salvo com id: {}", usuario.getId());

        return Map.of(
                "usuarioId", usuario.getId(),
                "email", usuario.getEmail()
        );
    }
}
