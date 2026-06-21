package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.UserService;
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
class ReleaseAccountWorkerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private ReleaseAccountWorker worker;

    @Test
    void liberarConta_deveChamarAtivarNoService() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of("usuarioId", "id-123"));
        doNothing().when(userService).activate("id-123");

        worker.releaseAccount(jobClient, activatedJob);

        verify(userService).activate("id-123");
    }
}
