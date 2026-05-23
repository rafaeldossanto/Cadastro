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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiberacaoContaWorkerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private LiberacaoContaWorker worker;

    private Usuario usuarioStub() {
        return Usuario.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    void liberarConta_deveAlterarStatusParaAtivo() {
        Usuario usuario = usuarioStub();
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-123"));
        when(usuarioRepository.findById("id-123")).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any())).thenReturn(usuario);

        worker.liberarConta(jobClient, activatedJob);

        assertThat(usuario.getStatus()).isEqualTo(StatusCadastro.ATIVO);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void liberarConta_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-inexistente"));
        when(usuarioRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> worker.liberarConta(jobClient, activatedJob))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario nao encontrado");
    }
}