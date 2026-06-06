package com.trail.Cadastro.model.dto.request;

import com.trail.Cadastro.model.enums.ProvedorAuth;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Requisicao de login social: o app ja obteve o ID token do provedor
 * (Google/Apple) e o envia aqui para o backend validar e criar/vincular a conta.
 */
public record LoginSocialRequest(
        @NotNull ProvedorAuth provedor,
        @NotBlank String idToken
) {}
