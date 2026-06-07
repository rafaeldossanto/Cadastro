package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.UsuarioService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LiberacaoContaWorkerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private LiberacaoContaWorker worker;

    @Test
    void liberarConta_deveChamarAtivarNoService() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-123"));
        doNothing().when(usuarioService).ativar("id-123");

        worker.liberarConta(jobClient, activatedJob);

        verify(usuarioService).ativar("id-123");
    }
}