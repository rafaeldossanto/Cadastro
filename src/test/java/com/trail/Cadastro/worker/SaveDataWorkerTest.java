package com.trail.Cadastro.worker;

import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.service.UserService;
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
class SaveDataWorkerTest {

    @Mock
    private UserService userService;

    @Mock
    private JobClient jobClient;

    @Mock
    private ActivatedJob activatedJob;

    @InjectMocks
    private SaveDataWorker worker;

    private UserDTO userDTOStub() {
        return UserDTO.builder()
                .id("usuario-1")
                .name("Rafael")
                .email("rafael@email.com")
                .userCode("rafael#1")
                .status(RegistrationStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void salvarDados_deveChamarServiceERetornarVariaveis() {
        when(activatedJob.getVariablesAsMap()).thenReturn(Map.of(
                "nome", "Rafael",
                "email", "rafael@email.com",
                "senha", "senha123"
        ));
        when(userService.create(any())).thenReturn(userDTOStub());

        Map<String, Object> result = worker.saveData(jobClient, activatedJob);

        assertThat(result).containsKey("usuarioId");
        assertThat(result).containsKey("email");
        assertThat(result.get("email")).isEqualTo("rafael@email.com");
        verify(userService).create(any());
    }
}
