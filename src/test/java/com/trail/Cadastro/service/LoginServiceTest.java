package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.IssuedToken;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.model.dto.response.AuthenticationResponse;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginService service;

    private User userStub(RegistrationStatus status) {
        return User.builder()
                .id("id-123")
                .name("Rafael")
                .email("rafael@email.com")
                .password("senha123")
                .userCode("rafael#1")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void login_deveRetornarTokenEUsuario_quandoCredenciaisValidasEContaAtiva() {
        User user = userStub(RegistrationStatus.ATIVO);
        when(userRepository.findByEmail("rafael@email.com")).thenReturn(user);
        when(tokenService.issue(user)).thenReturn(new IssuedToken("token-jwt", 7200L));

        AuthenticationResponse response = service.login("rafael@email.com", "senha123");

        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isEqualTo("token-jwt");
        assertThat(response.user().email()).isEqualTo("rafael@email.com");
    }

    @Test
    void login_deveFalhar_quandoEmailNaoExiste() {
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(null);

        assertThatThrownBy(() -> service.login("inexistente@email.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ou senha invalidos");

        verify(tokenService, never()).issue(any());
    }

    @Test
    void login_deveFalhar_quandoSenhaIncorreta() {
        when(userRepository.findByEmail("rafael@email.com"))
                .thenReturn(userStub(RegistrationStatus.ATIVO));

        assertThatThrownBy(() -> service.login("rafael@email.com", "senha-errada"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ou senha invalidos");

        verify(tokenService, never()).issue(any());
    }

    @Test
    void login_deveFalhar_quandoUsuarioSocialSemSenha() {
        User social = userStub(RegistrationStatus.ATIVO);
        social.setPassword(null);
        when(userRepository.findByEmail("rafael@email.com")).thenReturn(social);

        assertThatThrownBy(() -> service.login("rafael@email.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ou senha invalidos");

        verify(tokenService, never()).issue(any());
    }

    @Test
    void login_deveFalhar_quandoContaPendente() {
        when(userRepository.findByEmail("rafael@email.com"))
                .thenReturn(userStub(RegistrationStatus.PENDENTE));

        assertThatThrownBy(() -> service.login("rafael@email.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Confirme seu email para ativar a conta");

        verify(tokenService, never()).issue(any());
    }

    @Test
    void login_deveFalhar_quandoContaInativa() {
        when(userRepository.findByEmail("rafael@email.com"))
                .thenReturn(userStub(RegistrationStatus.INATIVO));

        // Mesma mensagem de credencial invalida: nao revela o estado da conta.
        assertThatThrownBy(() -> service.login("rafael@email.com", "senha123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ou senha invalidos");

        verify(tokenService, never()).issue(any());
    }
}
