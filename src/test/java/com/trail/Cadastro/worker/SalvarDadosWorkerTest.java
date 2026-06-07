package com.trail.Cadastro.worker;

import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.service.UsuarioService;
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
    private UsuarioService usuarioService;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private SalvarDadosWorker worker;

    private UsuarioDTO usuarioDTOStub() {
        return UsuarioDTO.builder()
                .id("usuario-1")
                .nome("Rafael")
                .email("rafael@email.com")
                .codigoUsuario("rafael#1")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    void salvarDados_deveChamarServiceERetornarVariaveis() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "nome", "Rafael",
                "email", "rafael@email.com",
                "senha", "senha123"
        ));
        when(usuarioService.create(any())).thenReturn(usuarioDTOStub());

        Map<String, Object> resultado = worker.salvarDados(jobClient, activatedJob);

        assertThat(resultado).containsKey("usuarioId");
        assertThat(resultado).containsKey("email");
        assertThat(resultado.get("email")).isEqualTo("rafael@email.com");
        verify(usuari