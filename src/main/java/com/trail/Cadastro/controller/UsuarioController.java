package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateReuqest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Slf4j
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDTO> create(@RequestBody @Valid UsuarioCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@RequestBody @Valid UsuarioUpdateReuqest request,
                                             @PathVariable String id) {
        return ResponseEntity.ok(usuarioService.update(request, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(usuarioService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}