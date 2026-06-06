package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public UsuarioDTO create(@RequestBody @Valid UsuarioCreateRequest request) {
        return usuarioService.create(request);
    }

    @PutMapping("/{id}")
    public UsuarioDTO update(@RequestBody @Valid UsuarioUpdateRequest request,
                             @PathVariable String id) {
        return usuarioService.update(request, id);
    }

    @GetMapping("/{id}")
    public UsuarioDTO getById(@PathVariable String id) {
        return usuarioService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        usuarioService.delete(id);
    }
}
