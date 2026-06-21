package com.trail.Cadastro.worker;

import com.trail.Cadastro.service.EmailService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailWorkerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private SendEmailWorker worker;

    @Test
    void enviarEmail_deveChamarEmailServiceERetornarToken() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "usuarioId", "id-123",
                "email", "rafael@email.com"
        ));
        when(emailService.sendConfirmation("id-123", "rafael@email.com")).thenReturn("token-abc");

        Map<String, Object> result = worker.sendEmail(jobClient, activatedJob);

        assertThat(result.get("tokenConfirmacao")).isEqualTo("token-abc");
        verify(emailService).sendConfirmation("id-123", "rafael@email.com");
    }
}
