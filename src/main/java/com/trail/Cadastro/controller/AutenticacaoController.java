package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.LoginSocialRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.service.AutenticacaoSocialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Login social. O app envia o ID token obtido do provedor; o backend valida e
 * devolve o usuario (criado ou vinculado). Retorna o DTO direto — erros vao
 * pelo GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/auth/social")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoSocialService autenticacaoSocialService;

    @PostMapping
    public UsuarioDTO login(@RequestBody @Valid LoginSocialRequest request) {
        return autenticacaoSocialService.autenticar(request.provedor(), request.idToken());
    }
}
