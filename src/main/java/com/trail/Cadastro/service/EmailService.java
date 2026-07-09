package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.EmailConfirmation;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.model.enums.ConfirmationStatus;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.repository.EmailConfirmationRepository;
import com.trail.Cadastro.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private static final int RESEND_COOLDOWN_SECONDS = 30;

    private final UserRepository repository;
    private final EmailConfirmationRepository emailRepository;
    private final JavaMailSender mailSender;

    @Value("${app.email.remetente}")
    private String sender;

    @Value("${app.email.confirmacao-url}")
    private String confirmationUrl;

    public String sendConfirmation(String userId, String email) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + userId));

        String token = UUID.randomUUID().toString();

        EmailConfirmation confirmation = EmailConfirmation.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .token(token)
                .status(ConfirmationStatus.PENDENTE)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .sentAt(LocalDateTime.now())
                .build();

        emailRepository.save(confirmation);

        send(email, user.getName(), token);

        return token;
    }

    /**
     * Reenvia o email de confirmacao para uma conta PENDENTE, respeitando o
     * intervalo minimo de 30s desde o ultimo envio. Gera um token novo; os
     * anteriores continuam validos ate expirarem. O reenvio NAO estende o prazo
     * do processo de cadastro — expirado o prazo, e preciso se cadastrar de novo.
     */
    public String resendConfirmation(String userId) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + userId));

        if (RegistrationStatus.ATIVO.equals(user.getStatus())) {
            throw new IllegalArgumentException("Email ja confirmado");
        }
        if (RegistrationStatus.INATIVO.equals(user.getStatus())) {
            throw new IllegalArgumentException("Cadastro expirado, faca um novo cadastro");
        }

        emailRepository.findFirstByUserIdOrderBySentAtDesc(userId).ifPresent(this::validateCooldown);

        log.info("Reenviando email de confirmacao para usuario: {}", userId);
        return sendConfirmation(userId, user.getEmail());
    }

    private void validateCooldown(EmailConfirmation last) {
        if (isNull(last.getSentAt())) {
            return;
        }
        LocalDateTime allowedAt = last.getSentAt().plusSeconds(RESEND_COOLDOWN_SECONDS);
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(allowedAt)) {
            long remaining = Math.max(1, Duration.between(now, allowedAt).getSeconds());
            throw new IllegalArgumentException(
                    "Aguarde " + remaining + " segundos para reenviar o email");
        }
    }

    private void send(String recipient, String name, String token) {
        String link = confirmationUrl + "?token=" + token;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(recipient);
            helper.setSubject("Confirme seu email - Trilha");
            helper.setText(buildBody(name, link), true);

            mailSender.send(message);
            log.info("Email de confirmacao enviado para: {}", recipient);
        } catch (MessagingException e) {
            log.error("Falha ao enviar email de confirmacao para {}: {}", recipient, e.getMessage());
            throw new IllegalStateException("Nao foi possivel enviar o email de confirmacao", e);
        }
    }

    private String buildBody(String name, String link) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: auto; color: #1a1a1a;">
                  <h2>Bem-vindo ao Trilha, %s!</h2>
                  <p>Falta um passo para ativar sua conta. Clique no botao abaixo para confirmar seu email:</p>
                  <p style="text-align: center; margin: 32px 0;">
                    <a href="%s" style="background: #2e7d32; color: #ffffff; padding: 14px 28px;
                       text-decoration: none; border-radius: 8px; display: inline-block;">
                      Confirmar email
                    </a>
                  </p>
                  <p>Se o botao nao funcionar, copie e cole este link no navegador:</p>
                  <p><a href="%s">%s</a></p>
                  <p style="color: #888888; font-size: 13px;">O link expira em 10 minutos. Se voce nao criou uma conta no Trilha, ignore este email.</p>
                </div>
                """.formatted(name, link, link, link);
    }
}
