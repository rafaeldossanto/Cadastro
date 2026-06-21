package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.EmailConfirmation;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.model.enums.ConfirmationStatus;
import com.trail.Cadastro.repository.EmailConfirmationRepository;
import com.trail.Cadastro.repository.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService")
class EmailServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private EmailConfirmationRepository emailRepository;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "sender", "nao-responda@trilha.com");
        ReflectionTestUtils.setField(service, "confirmationUrl", "http://localhost:8080/auth/confirmar-email");
    }

    private User userStub() {
        return User.builder()
                .id("id-123")
                .name("Rafael")
                .email("rafael@email.com")
                .status(RegistrationStatus.PENDENTE)
                .build();
    }

    @Test
    @DisplayName("sendConfirmation deve persistir token PENDENTE, enviar o email e retornar o token")
    void deveEnviarConfirmacao() {
        when(repository.findById("id-123")).thenReturn(Optional.of(userStub()));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        String token = service.sendConfirmation("id-123", "rafael@email.com");

        assertThat(token).isNotBlank();

        ArgumentCaptor<EmailConfirmation> captor = ArgumentCaptor.forClass(EmailConfirmation.class);
        verify(emailRepository).save(captor.capture());
        EmailConfirmation saved = captor.getValue();
        assertThat(saved.getToken()).isEqualTo(token);
        assertThat(saved.getStatus()).isEqualTo(ConfirmationStatus.PENDENTE);
        assertThat(saved.getExpiresAt()).isNotNull();

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendConfirmation deve falhar e nao enviar email quando o usuario nao existe")
    void deveFalharUsuarioInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.sendConfirmation("inexistente", "x@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario nao encontrado");

        verify(emailRepository, never()).save(any());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
