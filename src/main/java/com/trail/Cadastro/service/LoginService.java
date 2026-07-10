package com.trail.Cadastro.service;

import com.trail.Cadastro.auth.IssuedToken;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.mapper.AuthenticationMapper;
import com.trail.Cadastro.model.dto.response.AuthenticationResponse;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

/**
 * Login por email e senha. A senha informada e comparada com o hash BCrypt
 * persistido no cadastro. Exige conta ATIVA: PENDENTE ainda nao confirmou o
 * email; INATIVO foi desativada (responde como credencial invalida para nao
 * revelar o estado da conta). Usuarios sociais tem senha nula e nao autenticam
 * por aqui.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationResponse login(String email, String password) {
        log.info("Login por senha para {}", email);

        User user = userRepository.findByEmail(email);
        if (isNull(user) || isNull(user.getPassword()) || !passwordEncoder.matches(password, user.getPassword())
                || RegistrationStatus.INATIVO.equals(user.getStatus())) {
            throw new IllegalArgumentException("Email ou senha invalidos");
        }

        if (RegistrationStatus.PENDENTE.equals(user.getStatus())) {
            throw new IllegalArgumentException("Confirme seu email para ativar a conta");
        }

        IssuedToken token = tokenService.issue(user);
        return AuthenticationMapper.toResponse(user, token);
    }
}
