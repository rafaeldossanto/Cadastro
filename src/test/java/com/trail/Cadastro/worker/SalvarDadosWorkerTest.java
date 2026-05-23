package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.UsuarioRepository;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalvarDadosWorkerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private SalvarDadosWorker worker;

    private Usuario usuarioStub() {
        return Usuario.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .senha("senha123")
                .codigoUsuario("rafael#1")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    void salvarDados_deveSalvarUsuarioERetornarVariaveis() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "nome", "Rafael",
                "email", "rafael@email.com",
                "senha", "senha123"
        ));
        when(usuarioRepository.proximaSequencia()).thenReturn(1L);
        when(usuarioRepository.save(any())).thenReturn(usuarioStub());

        Map<String, Object> resultado = worker.salvarDados(jobClient, activatedJob);

        assertThat(resultado).containsKey("usuarioId");
        assertThat(resultado).containsKey("email");
        assertThat(resultado.get("email")).isEqualTo("rafael@email.com");
        verify(usuarioRepository).save(any(Usuario.class));
    }
}