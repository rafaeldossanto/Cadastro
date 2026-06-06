package com.trail.Cadastro.model.enums;

/**
 * Provedor de autenticacao de uma conta vinculada.
 *
 * LOCAL  = cadastro tradicional por email/senha neste sistema.
 * GOOGLE / APPLE = login social (OAuth2/OpenID Connect).
 *
 * Para adicionar um novo provedor (Facebook, GitHub, etc.) basta incluir aqui
 * e registrar o verificador de token correspondente — o restante do fluxo
 * (criar/vincular conta) e generico.
 */
public enum ProvedorAuth {
    LOCAL,
    GOOGLE,
    APPLE
}
