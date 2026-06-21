package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.request.UserUpdateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController controller;

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

    @Test
    void create_deveRetornarDTO_quandoSucesso() {
        when(userService.create(any())).thenReturn(userDTOStub());

        UserDTO result = controller.create(
                new UserCreateRequest("Rafael", "rafael@email.com", "senha123")
        );

        assertThat(result).isNotNull();
        assertThat(result.email()).isEqualTo("rafael@email.com");
    }

    @Test
    void getById_deveRetornarDTO_quandoUsuarioExiste() {
        when(userService.getById("id-123")).thenReturn(userDTOStub());

        UserDTO result = controller.getById("id-123");

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("Rafael");
    }

    @Test
    void update_deveRetornarDTO_quandoSucesso() {
        when(userService.update(any(), eq("id-123"))).thenReturn(userDTOStub());

        UserDTO result = controller.update(
                new UserUpdateRequest("Rafael Atualizado", null), "id-123"
        );

        assertThat(result).isNotNull();
        verify(userService).update(any(), eq("id-123"));
    }

    @Test
    void delete_deveChamarService_quandoSucesso() {
        doNothing().when(userService).delete("id-123");

        controller.delete("id-123");

        verify(userService).delete("id-123");
    }
}
