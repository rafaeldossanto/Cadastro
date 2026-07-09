package com.trail.Cadastro.service;

import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import io.camunda.zeebe.client.ZeebeClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Orquestra o cadastro: persiste o usuario (UserService) e inicia a instancia
 * do processo Camunda que conduz o restante — envio/confirmacao de email e
 * aceite de termos em paralelo, liberando a conta ao final ou deletando os
 * dados se o prazo do processo expirar.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationService {

    private static final String PROCESS_ID = "cadastro-usuario";

    private final UserService userService;
    private final ZeebeClient zeebeClient;

    public UserDTO register(UserCreateRequest request) {
        UserDTO user = userService.create(request);

        try {
            zeebeClient.newCreateInstanceCommand()
                    .bpmnProcessId(PROCESS_ID)
                    .latestVersion()
                    .variables(Map.of(
                            "usuarioId", user.id(),
                            "email", user.email()))
                    .send()
                    .join();
        } catch (Exception e) {
            // Sem o processo nao ha email de confirmacao nem timer de limpeza:
            // desativa o registro para liberar o email para nova tentativa.
            userService.delete(user.id());
            throw new IllegalStateException("Nao foi possivel iniciar o processo de cadastro", e);
        }

        log.info("Processo de cadastro iniciado para usuario: {}", user.id());
        return user;
    }
}
