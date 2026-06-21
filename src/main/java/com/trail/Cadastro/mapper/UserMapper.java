package com.trail.Cadastro.mapper;

import com.trail.Cadastro.auth.ProviderUserData;
import com.trail.Cadastro.entity.User;
import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.model.enums.RegistrationStatus;
import com.trail.Cadastro.utils.GenerateUtil;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.UUID;

@UtilityClass
public class UserMapper {

    public User toEntity(UserCreateRequest request, Long sequence) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(request.name())
                .email(request.email())
                .password(request.password())
                .userCode(GenerateUtil.makeCode(request.name(), sequence))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Cria um User a partir dos dados validados de um provedor social.
     * Pontos importantes:
     * - Gera o userCode (nome + sequencia) igual ao cadastro normal, para
     *   que o usuario social possa ser encontrado/adicionado como amigo.
     * - Entra como ATIVO: o provedor (Google/Apple) ja confirmou o email, entao
     *   nao ha etapa de confirmacao por email.
     * - Senha fica nula: a autenticacao e delegada ao provedor.
     */
    public User toEntitySocial(ProviderUserData data, String name, Long sequence) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .email(data.email())
                .userCode(GenerateUtil.makeCode(name, sequence))
                .status(RegistrationStatus.ATIVO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Cria um User para o login de desenvolvimento: espelha o usuario social
     * (userCode gerado, ATIVO, senha nula), porem a partir de email/nome
     * informados direto, sem provedor. So usado pelo fluxo {@code @Profile("dev")}.
     */
    public User toEntityDev(String email, String name, Long sequence) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .email(email)
                .userCode(GenerateUtil.makeCode(name, sequence))
                .status(RegistrationStatus.ATIVO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public UserDTO toResponse(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .userCode(user.getUserCode())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
