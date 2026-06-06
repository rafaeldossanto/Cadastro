package com.trail.Cadastro.controller;

import com.trail.Cadastro.service.ConfirmacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ConfirmacaoController {

    private final ConfirmacaoService confirmacaoService;

    @GetMapping("/confirmar-email")
    public String confirmarEmail(@RequestParam String token) {
        confirmacaoService.confirmarEmail(token);
        return "Email confirmado com sucesso";
    }

    @PostMapping("/aceitar-termos")
    public String aceitarTermos(@RequestParam String usuarioId,
                                @RequestParam String versaoTermos) {
        confirmacaoService.aceitarTermos(usuarioId, versaoTermos);
        return "Termos aceitos com sucesso";
    }
}
