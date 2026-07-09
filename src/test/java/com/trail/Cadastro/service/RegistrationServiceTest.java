package com.trail.Cadastro.service;

import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import io.camunda.zeebe.client.ZeebeClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient zeebeClient;

    @InjectMocks
    private RegistrationService service;

    private UserDTO userDTOStub() {
        return UserDTO.builder()
                .id("id-123")
                .name("Rafael")
                .email("rafael@email.com")
                .userCode("rafael#1")
                .status(RegistrationStatus.PENDENTE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private UserCreateRequest createRequestStub() {
        return new UserCreateRequest("Rafael", "rafael@email.com", "senha123");
    }

    @Test
    void register_deveCriarUsuarioEIniciarProcesso() {
        when(userService.create(any())).thenReturn(userDTOStub());

        UserDTO result = service.register(createRequestStub());

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("id-123");
        verify(userService).create(any(UserCreateRequest.class));
        verify(zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("cadastro-usuario")
                .latestVersion())
                .variables(Map.of("usuarioId", "id-123", "email", "rafael@email.com"));
    }

    @Test
    void register_deveDesativarUsuario_quandoInicioDoProcessoFalha() {
        when(userService.create(any())).thenReturn(userDTOStub());
        when(zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("cadastro-usuario")
                .latestVersion()
                .variables(Map.of("usuarioId", "id-123", "email", "rafael@email.com"))
                .send()
                .join())
                .thenThrow(new RuntimeException("zeebe indisponivel"));

        assertThatThrownBy(() -> service.register(createRequestStub()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("processo de cadastro");

        // Compensacao: desativa o registro para liberar o email para nova tentativa.
        verify(userService).delete("id-123");
    }

    @Test
    void register_naoDeveIniciarProcesso_quandoCriacaoFalha() {
        when(userService.create(any()))
                .thenThrow(new IllegalArgumentException("Conta com esse email ja existente"));

        assertThatThrownBy(() -> service.register(createRequestStub()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Conta com esse email ja existente");

        verifyNoInteractions(zeebeClient);
    }
}
