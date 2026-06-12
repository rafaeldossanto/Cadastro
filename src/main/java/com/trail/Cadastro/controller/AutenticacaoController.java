package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.LoginSocialRequest;
import com.trail.Cadastro.model.dto.response.AutenticacaoResponse;
import com.trail.Cadastro.service.AutenticacaoSocialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login social. O app envia o ID token obtido do provedor; o backend valida,
 * resolve o usuario (criado ou vinculado) e devolve o usuario junto do access
 * token da aplicacao, que o app deve enviar no header Authorization a seguir.
 */
@RestController
@RequestMapping("/auth/social")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoSocialService autenticacaoSocialService;

    @PostMapping
    public AutenticacaoResponse login(@RequestBody @Valid LoginSocialRequest request) {
        return autenticacaoSocialService.autenticar(request.provedor(), request.idToken());
    }
}
