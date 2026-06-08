package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.model.enums.StatusConfirmacao;
import com.trail.Cadastro.repository.ConfirmacaoEmailRepository;
import com.trail.Cadastro.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final UsuarioRepository repository;
    private final ConfirmacaoEmailRepository emailRepository;
    private final JavaMailSender mailSender;

    @Value("${app.email.remetente}")
    private String remetente;

    @Value("${app.email.confirmacao-url}")
    private String confirmacaoUrl;

    public String enviarConfirmacao(String usuarioId, String email) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));

        String token = UUID.randomUUID().toString();

        ConfirmacaoEmail confirmacao = ConfirmacaoEmail.builder()
                .usuario(usuario)
                .token(token)
                .status(StatusConfirmacao.PENDENTE)
                .expiraEm(LocalDateTime.now().plusMinutes(10))
                .build();

        emailRepository.save(confirmacao);

        enviar(email, usuario.getNome(), token);

        return token;
    }

    private void enviar(String destinatario, String nome, String token) {
        String link = confirmacaoUrl + "?token=" + token;
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, false, "UTF-8");
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Confirme seu email - Trilha");
            helper.setText(montarCorpo(nome, link), true);

            mailSender.send(mensagem);
            log.info("Email de confirmacao enviado para: {}", destinatario);
        } catch (MessagingException e) {
            log.error("Falha ao enviar email de confirmacao para {}: {}", destinatario, e.getMessage());
            throw new IllegalStateException("Nao foi possivel enviar o email de confirmacao", e);
        }
    }

    private String montarCorpo(String nome, String link) {
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
                """.formatted(nome, link, link, link);
    }
}
