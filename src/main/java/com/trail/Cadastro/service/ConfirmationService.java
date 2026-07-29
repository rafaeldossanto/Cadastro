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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationService {

    private final EmailConfirmationRepository confirmationRepository;
    private final TermsAcceptanceRepository termsRepository;
    private final UserRepository userRepository;
    private final ZeebeClient zeebeClient;

    public void confirmEmail(String token) {
        // O token NAO vai para o log: ele e a credencial que ativa a conta. Quem
        // lesse o log (ou um agregador de logs) poderia confirmar conta alheia.
        log.info("Confirmando email");

        EmailConfirmation confirmation = confirmationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token invalido"));

        if (ConfirmationStatus.CONFIRMADO.equals(confirmation.getStatus())) {
            throw new IllegalArgumentException("Email ja confirmado");
        }

        if (confirmation.getExpiresAt().isBefore(LocalDateTime.now())) {
            confirmation.setStatus(ConfirmationStatus.EXPIRADO);
            confirmationRepository.save(confirmation);
            throw new IllegalArgumentException("Token expirado");
        }

        // Um token reenviado pode sobreviver a conta (o timer do processo deleta
        // aos 10 min) ou chegar depois da ativacao por outro token/login social:
        // valida o status do dono antes de avancar o processo.
        RegistrationStatus userStatus = confirmation.getUser().getStatus();
        if (RegistrationStatus.ATIVO.equals(userStatus)) {
            throw new IllegalArgumentException("Email ja confirmado");
        }
        if (RegistrationStatus.INATIVO.equals(userStatus)) {
            throw new IllegalArgumentException("Cadastro expirado, faca um novo cadastro");
        }

        // Publica antes de gravar CONFIRMADO: se o publish falhar, o status continua
        // PENDENTE e o usuario pode tentar o link de novo (a guarda de "ja confirmado"
        // bloquearia a retentativa se a ordem fosse invertida).
        zeebeClient.newPublishMessageCommand()
                .messageName("email-confirmado")
                .correlationKey(confirmation.getUser().getId())
                .send()
                .join();

        confirmation.setStatus(ConfirmationStatus.CONFIRMADO);
        confirmation.setConfirmedAt(LocalDateTime.now());
        confirmationRepository.save(confirmation);

        log.info("Email confirmado para usuario: {}", confirmation.getUser().getId());
    }

    public void acceptTerms(String userId) {
        log.info("Aceitando termos para usuario: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

        TermsAcceptance terms = TermsAcceptance.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .accepted(true)
                .acceptedAt(LocalDateTime.now())
                .build();

        termsRepository.save(terms);

        zeebeClient.newPublishMessageCommand()
                .messageName("termos-aceitos")
                .correlationKey(userId)
                .send()
                .join();

        log.info("Termos aceitos para usuario: {}", userId);
    }
}
