package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.model.enums.StatusConfirmacao;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
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
    private UsuarioRepository repository;
    @Mock
    private ConfirmacaoEmailRepository emailRepository;
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "remetente", "nao-responda@trilha.com");
        ReflectionTestUtils.setField(service, "confirmacaoUrl", "http://localhost:8080/auth/confirmar-email");
    }

    private Usuario usuarioStub() {
        return Usuario.builder()
                .id("id-123")
                .nome("Rafael")
                .email("rafael@email.com")
                .status(StatusCadastro.PENDENTE)
                .build();
    }

    @Test
    @DisplayName("enviarConfirmacao deve persistir token PENDENTE, enviar o email e retornar o token")
    void deveEnviarConfirmacao() {
        when(repository.findById("id-123")).thenReturn(Optional.of(usuarioStub()));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        String token = service.enviarConfirmacao("id-123", "rafael@email.com");

        assertThat(token).isNotBlank();

        ArgumentCaptor<ConfirmacaoEmail> captor = ArgumentCaptor.forClass(ConfirmacaoEmail.class);
        verify(emailRepository).save(captor.capture());
        ConfirmacaoEmail salvo = captor.getValue();
        assertThat(salvo.getToken()).isEqualTo(token);
        assertThat(salvo.getStatus()).isEqualTo(StatusConfirmacao.PENDENTE);
        assertThat(salvo.getExpiraEm()).isNotNull();

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("enviarConfirmacao deve falhar e nao enviar email quando o usuario nao existe")
    void deveFalharUsuarioInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enviarConfirmacao("inexistente", "x@email.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Usuario nao encontrado");

        verify(emailRepository, never()).save(any());
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}
