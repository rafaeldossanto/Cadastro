package com.trail.Cadastro.controller;

import com.trail.Cadastro.service.ConfirmationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ConfirmationController {

    private final ConfirmationService confirmationService;

    @GetMapping("/confirmar-email")
    public String confirmEmail(@RequestParam String token) {
        confirmationService.confirmEmail(token);
        return "Email confirmado com sucesso";
    }

    @PostMapping("/aceitar-termos")
    public String acceptTerms(@RequestParam("usuarioId") String userId) {
        confirmationService.acceptTerms(userId);
        return "Termos aceitos com sucesso";
    }
}
