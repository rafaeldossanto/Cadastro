package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.DadosUsuarioProvedor;
import com.trail.Cadastro.auth.VerificadorTokenSocial;
import com.trail.Cadastro.entity.ContaVinculada;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.ProvedorAuth;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.ContaVinculadaRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AutenticacaoSocialService")
class AutenticacaoSocialServiceTest {

    private static final String ID_TOKEN = "token-jwt";
    private static final String SUB = "google-sub-123";
    private static final String EMAIL = "rafael@gmail.com";

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private ContaVinculadaRepository contaVinculadaRepository;
    @Mock
    private VerificadorTokenSocial googleVerificador;

    private AutenticacaoSocialService service;

    @BeforeEach
    void setUp() {
        when(googleVerificador.provedor()).thenReturn(ProvedorAuth.GOOGLE);
        service = new AutenticacaoSocialService(
                usuarioRepository, contaVinculadaRepository, List.of(googleVerificador));
    }

    private DadosUsuarioProvedor dados() {
        return new DadosUsuarioProvedor(SUB, EMAIL, "Rafael");
    }

    private Usuario usuarioExistente() {
        return Usuario.builder()
                .id("usuario-1").nome("Rafael").email(EMAIL)
                .codigoUsuario("rafael#1").status(StatusCadastro.ATIVO)
                .dataCriacao(LocalDateTime.now()).dataAtualizacao(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("cenario 1: vinculo ja existe -> loga o usuario vinculado, sem criar nem vincular de novo")
    void deveLogarQuandoVinculoExiste() {
        Usuario usuario = usuarioExistente();
        ContaVinculada vinculo = ContaVinculada.builder().usuario(usuario).build();

        when(googleVerificador.verificar(ID_TOKEN)).thenReturn(dados());
        when(contaVinculadaRepository.findByProvedorAndProvedorUsuarioId(ProvedorAuth.GOOGLE, SUB))
                .thenReturn(Optional.of(vinculo));

        UsuarioDTO response = service.autenticar(ProvedorAuth.GOOGLE, ID_TOKEN);

        assertThat(response.id()).isEqualTo("usuario-1");
        verify(usuarioRepository, never()).save(any());
        verify(contaVinculadaRepository, never()).save(any());
    }

    @Test
    @DisplayName("cenario 2: email ja existe sem vinculo -> vincula o provedor a conta existente, sem criar usuario")
    void deveVincularAContaExistente() {
        Usuario usuario = usuarioExistente();

        when(googleVerificador.verificar(ID_TOKEN)).thenReturn(dados());
        when(contaVinculadaRepository.findByProvedorAndProvedorUsuarioId(ProvedorAuth.GOOGLE, SUB))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(usuario);

        UsuarioDTO response = service.autenticar(ProvedorAuth.GOOGLE, ID_TOKEN);

        assertThat(response.id()).isEqualTo("usuario-1");
        verify(usuarioRepository, never()).save(any());
        verify(contaVinculadaRepository).save(any(ContaVinculada.class));
    }

    @Test
    @DisplayName("cenario 3: usuario novo -> cria com codigoUsuario e status ATIVO, e vincula o provedor")
    void deveCriarUsuarioNovo() {
        when(googleVerificador.verificar(ID_TOKEN)).thenReturn(dados());
        when(contaVinculadaRepository.findByProvedorAndProvedorUsuarioId(ProvedorAuth.GOOGLE, SUB))
                .thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(null);
        when(usuarioRepository.proximaSequencia()).thenReturn(7L);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO response = service.autenticar(ProvedorAuth.GOOGLE, ID_TOKEN);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario criado = captor.getValue();

        assertThat(criado.getCodigoUsuario()).isEqualTo("rafael#7");
        assertThat(criado.getStatus()).isEqualTo(StatusCadastro.ATIVO);
        assertThat(criado.getSenha()).isNull();
        assertThat(response.email()).isEqualTo(EMAIL);
        verify(contaVinculadaRepository).save(any(ContaVinculada.class));
    }

    @Test
    @DisplayName("deve falhar quando o provedor nao tem verificador registrado")
    void deveFalharProvedorNaoSuportado() {
        assertThatThrownBy(() -> service.autenticar(ProvedorAuth.APPLE, ID_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao suportado");
    }

    @Test
    @DisplayName("deve propagar erro quando o token e invalido")
    void devePropagarTokenInvalido() {
        when(googleVerificador.verificar(ID_TOKEN))
                .thenThrow(new IllegalArgumentException("Token do Google invalido"));

        assertThatThrownBy(() -> service.autenticar(ProvedorAuth.GOOGLE, ID_TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("invalido");

        verify(usuarioRepository, never()).save(any());
    }
}
