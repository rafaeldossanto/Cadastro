package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.TermosAceiteRepository;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DelecaoDadosWorkerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfirmacaoEmailRepository confirmacaoEmailRepository;

    @Mock
    private TermosAceiteRepository termosAceiteRepository;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private DelecaoDadosWorker worker;

    private Usuario usuarioStub(StatusCadastro status) {
        return Usuario.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .status(status)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    void deletarDados_deveDeletarTudo_quandoStatusPendente() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-123"));
        when(usuarioRepository.findById("id-123")).thenReturn(Optional.of(usuarioStub(StatusCadastro.PENDENTE)));

        worker.deletarDados(jobClient, activatedJob);

        verify(confirmacaoEmailRepository).deleteByUsuarioId("id-123");
        verify(termosAceiteRepository).deleteByUsuarioId("id-123");
        verify(usuarioRepository).delete(any(Usuario.class));
    }

    @Test
    void deletarDados_naoDeveDeletar_quandoUsuarioJaAtivo() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-123"));
        when(usuarioRepository.findById("id-123")).thenReturn(Optional.of(usuarioStub(StatusCadastro.ATIVO)));

        worker.deletarDados(jobClient, activatedJob);

        verify(confirmacaoEmailRepository, never()).deleteByUsuarioId(any());
        verify(termosAceiteRepository, never()).deleteByUsuarioId(any());
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }

    @Test
    void deletarDados_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-inexistente"));
        when(usuarioRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> worker.deletarDados(jobClient, activatedJob))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario nao encontrado");
    }
}