package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateReuqest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    // ---- stubs ----

    private UsuarioDTO usuarioDTOStub() {
        return UsuarioDTO.builder()
                .nome("Rafael")
                .email("rafael@email.com")
                .codigoUsuario("rafael#1")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    // ---- create ----

    @Test
    void create_deveRetornar201_quandoSucesso() {
        when(usuarioService.create(any())).thenReturn(usuarioDTOStub());

        ResponseEntity<UsuarioDTO> response = controller.create(
                new UsuarioCreateRequest("Rafael", "rafael@email.com", "senha123")
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("rafael@email.com");
    }

    // ---- getById ----

    @Test
    void getById_deveRetornar200_quandoUsuarioExiste() {
        when(usuarioService.getById("id-123")).thenReturn(usuarioDTOStub());

        ResponseEntity<UsuarioDTO> response = controller.getById("id-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().nome()).isEqualTo("Rafael");
    }

    // ---- update ----

    @Test
    void update_deveRetornar200_quandoSucesso() {
        when(usuarioService.update(any(), eq("id-123"))).thenReturn(usuarioDTOStub());

        ResponseEntity<UsuarioDTO> response = controller.update(
                new UsuarioUpdateReuqest("Rafael Atualizado", null), "id-123"
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        verify(usuarioService).update(any(), eq("id-123"));
    }

    // ---- delete ----

    @Test
    void delete_deveRetornar204_quandoSucesso() {
        doNothing().when(usuarioService).delete("id-123");

        ResponseEntity<Void> response = controller.delete("id-123");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(usuarioService).delete("id-123");
    }
}