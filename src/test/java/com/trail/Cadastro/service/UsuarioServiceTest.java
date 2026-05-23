package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateReuqest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    // ---- stubs reutilizaveis ----

    private Usuario usuarioStub() {
        return Usuario.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .senha("senha123")
                .codigoUsuario("rafael#1")
                .status(StatusCadastro.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .dataAtualizacao(LocalDateTime.now())
                .build();
    }

    private UsuarioCreateRequest createRequestStub() {
        return new UsuarioCreateRequest("Rafael", "rafael@email.com", "senha123");
    }

    // ---- create ----

    @Test
    void create_deveSalvarERetornarDTO_quandoEmailNaoExiste() {
        when(repository.findByEmail("rafael@email.com")).thenReturn(null);
        when(repository.proximaSequencia()).thenReturn(1L);
        when(repository.save(any())).thenReturn(usuarioStub());

        UsuarioDTO resultado = service.create(createRequestStub());

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("rafael@email.com");
        assertThat(resultado.nome()).isEqualTo("Rafael");
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void create_deveLancarExcecao_quandoEmailJaExiste() {
        when(repository.findByEmail("rafael@email.com")).thenReturn(usuarioStub());

        assertThatThrownBy(() -> service.create(createRequestStub()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Conta com esse email ja existente");
    }

    // ---- getById ----

    @Test
    void getById_deveRetornarDTO_quandoUsuarioExiste() {
        when(repository.findById("id-123")).thenReturn(Optional.of(usuarioStub()));

        UsuarioDTO resultado = service.getById("id-123");

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo("rafael@email.com");
    }

    @Test
    void getById_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("id-inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario nao encontrado");
    }

    // ---- update ----

    @Test
    void update_deveAtualizarNome_quandoEmailNulo() {
        Usuario usuario = usuarioStub();
        when(repository.findById("id-123")).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenReturn(usuario);

        UsuarioUpdateReuqest request = new UsuarioUpdateReuqest("Rafael Novo", null);
        UsuarioDTO resultado = service.update(request, "id-123");

        assertThat(resultado).isNotNull();
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void update_deveAtualizarEmail_quandoNomeNulo() {
        Usuario usuario = usuarioStub();
        when(repository.findById("id-123")).thenReturn(Optional.of(usuario));
        when(repository.findByEmail("novo@email.com")).thenReturn(null);
        when(repository.save(any())).thenReturn(usuario);

        UsuarioUpdateReuqest request = new UsuarioUpdateReuqest(null, "novo@email.com");
        UsuarioDTO resultado = service.update(request, "id-123");

        assertThat(resultado).isNotNull();
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void update_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UsuarioUpdateReuqest("nome", null), "id-inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario nao encontrado");
    }

    // ---- delete ----

    @Test
    void delete_deveInativarUsuario_quandoExiste() {
        Usuario usuario = usuarioStub();
        when(repository.findById("id-123")).thenReturn(Optional.of(usuario));
        when(repository.save(any())).thenReturn(usuario);

        service.delete("id-123");

        assertThat(usuario.getStatus()).isEqualTo(StatusCadastro.INATIVO);
        verify(repository).save(usuario);
    }

    @Test
    void delete_deveLancarExcecao_quandoUsuarioNaoExiste() {
        when(repository.findById("id-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("id-inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario nao encontrado");
    }
}