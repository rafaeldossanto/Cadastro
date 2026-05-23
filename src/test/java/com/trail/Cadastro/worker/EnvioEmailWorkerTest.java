package com.trail.Cadastro.worker;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
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
class EnvioEmailWorkerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ConfirmacaoEmailRepository confirmacaoEmailRepository;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private EnvioEmailWorker worker;

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
    void enviarEmail_deveSalvarConfirmacaoERetornarToken() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "usuarioId", "id-123",
                "email", "rafael@email.com"
        ));
        when(usuarioRepository.findById("id-123")).thenReturn(Optional.of(usuarioStub()));
        when(confirmacaoEmailRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Map<String, Object> resultado = worker.enviarEmail(jobClient, activatedJob);

        assertThat(resultado).containsKey("tokenConfirmacao");
        assertThat(resultado.get("tokenConfirmacao").toString()).isNotBlank();
        verify(confirmacaoEmailRepository).save(any(ConfirmacaoEmail.class));
    }

    @Test
    void enviarEmail_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "usuarioId", "id-inexistente",
                "email", "rafael@email.com"
        ));
        when(usuarioRepository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> worker.enviarEmail(jobClient, activatedJob))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Usuario nao encontrado");
    }
}