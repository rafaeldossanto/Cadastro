package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.EmailConfirmation;
import com.trail.Cadastro.entity.TermsAcceptance;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.model.enums.ConfirmationStatus;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.repository.EmailConfirmationRepository;
import com.trail.Cadastro.repository.TermsAcceptanceRepository;
import com.trail.Cadastro.repository.UserRepository;
import io.camunda.zeebe.client.ZeebeClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfirmationService")
class ConfirmationServiceTest {

    @Mock
    private EmailConfirmationRepository confirmationRepository;
    @Mock
    private TermsAcceptanceRepository termsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ZeebeClient zeebeClient;

    @InjectMocks
    private ConfirmationService service;

    private User userStub() {
        return User.builder()
                .id("id-123")
                .name("Rafael")
                .email("rafael@email.com")
                .build();
    }

    private EmailConfirmation confirmationStub(ConfirmationStatus status, LocalDateTime expiresAt) {
        return EmailConfirmation.builder()
                .id("conf-1")
                .user(userStub())
                .token("token-abc")
                .status(status)
                .expiresAt(expiresAt)
                .build();
    }

    // ---- confirmEmail ----

    @Test
    @DisplayName("confirmEmail deve publicar email-confirmado e gravar CONFIRMADO quando token valido")
    void deveConfirmarEmail() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.PENDENTE, LocalDateTime.now().plusMinutes(5));
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));

        service.confirmEmail("token-abc");

        verify(zeebeClient.newPublishMessageCommand().messageName("email-confirmado"))
                .correlationKey("id-123");
        assertThat(confirmation.getStatus()).isEqualTo(ConfirmationStatus.CONFIRMADO);
        assertThat(confirmation.getConfirmedAt()).isNotNull();
        verify(confirmationRepository).save(confirmation);
    }

    @Test
    @DisplayName("confirmEmail deve falhar quando o token nao existe")
    void deveFalharTokenInvalido() {
        when(confirmationRepository.findByToken("token-x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.confirmEmail("token-x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token invalido");

        verifyNoInteractions(zeebeClient);
    }

    @Test
    @DisplayName("confirmEmail deve falhar sem republicar quando ja confirmado")
    void deveFalharJaConfirmado() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.CONFIRMADO, LocalDateTime.now().plusMinutes(5));
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));

        assertThatThrownBy(() -> service.confirmEmail("token-abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ja confirmado");

        verifyNoInteractions(zeebeClient);
        verify(confirmationRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirmEmail deve marcar EXPIRADO e falhar quando o token venceu")
    void deveFalharTokenExpirado() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.PENDENTE, LocalDateTime.now().minusMinutes(1));
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));

        assertThatThrownBy(() -> service.confirmEmail("token-abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Token expirado");

        assertThat(confirmation.getStatus()).isEqualTo(ConfirmationStatus.EXPIRADO);
        verify(confirmationRepository).save(confirmation);
        verifyNoInteractions(zeebeClient);
    }

    @Test
    @DisplayName("confirmEmail deve falhar quando o dono do token ja esta ATIVO (confirmou por outro caminho)")
    void deveFalharDonoJaAtivo() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.PENDENTE, LocalDateTime.now().plusMinutes(5));
        confirmation.getUser().setStatus(RegistrationStatus.ATIVO);
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));

        assertThatThrownBy(() -> service.confirmEmail("token-abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ja confirmado");

        verifyNoInteractions(zeebeClient);
        verify(confirmationRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirmEmail deve falhar quando a conta ja foi deletada pelo timer (token reenviado tardio)")
    void deveFalharDonoInativo() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.PENDENTE, LocalDateTime.now().plusMinutes(5));
        confirmation.getUser().setStatus(RegistrationStatus.INATIVO);
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));

        assertThatThrownBy(() -> service.confirmEmail("token-abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cadastro expirado, faca um novo cadastro");

        verifyNoInteractions(zeebeClient);
        verify(confirmationRepository, never()).save(any());
    }

    @Test
    @DisplayName("confirmEmail deve manter PENDENTE quando o publish falha, permitindo retentativa")
    void deveManterPendenteQuandoPublishFalha() {
        EmailConfirmation confirmation =
                confirmationStub(ConfirmationStatus.PENDENTE, LocalDateTime.now().plusMinutes(5));
        when(confirmationRepository.findByToken("token-abc")).thenReturn(Optional.of(confirmation));
        when(zeebeClient.newPublishMessageCommand()
                .messageName("email-confirmado")
                .correlationKey("id-123")
                .send()
                .join())
                .thenThrow(new RuntimeException("zeebe indisponivel"));

        assertThatThrownBy(() -> service.confirmEmail("token-abc"))
                .isInstanceOf(RuntimeException.class);

        assertThat(confirmation.getStatus()).isEqualTo(ConfirmationStatus.PENDENTE);
        verify(confirmationRepository, never()).save(any());
    }

    // ---- acceptTerms ----

    @Test
    @DisplayName("acceptTerms deve gravar o aceite com id e publicar termos-aceitos")
    void deveAceitarTermos() {
        when(userRepository.findById("id-123")).thenReturn(Optional.of(userStub()));

        service.acceptTerms("id-123");

        ArgumentCaptor<TermsAcceptance> captor = ArgumentCaptor.forClass(TermsAcceptance.class);
        verify(termsRepository).save(captor.capture());
        TermsAcceptance saved = captor.getValue();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAccepted()).isTrue();
        assertThat(saved.getAcceptedAt()).isNotNull();

        verify(zeebeClient.newPublishMessageCommand().messageName("termos-aceitos"))
                .correlationKey("id-123");
    }

    @Test
    @DisplayName("acceptTerms deve falhar sem gravar nem publicar quando o usuario nao existe")
    void deveFalharAceiteUsuarioInexistente() {
        when(userRepository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptTerms("inexistente"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Usuario nao encontrado");

        verify(termsRepository, never()).save(any());
        verifyNoInteractions(zeebeClient);
    }
}
