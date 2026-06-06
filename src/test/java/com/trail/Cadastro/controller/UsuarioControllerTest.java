package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.service.UsuarioService;
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
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController controller;

    private UsuarioDTO usuarioDTOStub() {
        return UsuarioDTO.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .codigoUsuario("rafael#1")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    void create_deveRetornarDTO_quandoSucesso() {
        when(usuarioService.create(any())).thenReturn(usuarioDTOStub());

        UsuarioDTO resultado = controller.create(
                new UsuarioCreateRequest("Rafael", "rafael@email.com", "senha123")
        );

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("rafael@email.com");
    }

    @Test
    void getById_deveRetornarDTO_quandoUsuarioExiste() {
        when(usuarioService.getById("id-123")).thenReturn(usuarioDTOStub());

        UsuarioDTO resultado = controller.getById("id-123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.nome()).isEqualTo("Rafael");
    }

    @Test
    void update_deveRetornarDTO_quandoSucesso() {
        when(usuarioService.update(any(), eq("id-123"))).thenReturn(usuarioDTOStub());

        UsuarioDTO resultado = controller.update(
                new UsuarioUpdateRequest("Rafael Atualizado", null), "id-123"
        );

        assertThat(resultado).isNotNull();
        verify(usuarioService).update(any(), eq("id-123"));
    }

    @Test
    void delete_deveChamarService_quandoSucesso() {
        doNothing().when(usuarioService).delete("id-123");

        controller.delete("id-123");

        verify(usuarioService).delete("id-123");
    }
}
