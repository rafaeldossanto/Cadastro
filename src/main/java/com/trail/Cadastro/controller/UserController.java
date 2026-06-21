package com.trail.Cadastro.controller;

import com.trail.Cadastro.model.dto.request.UserCreateRequest;
import com.trail.Cadastro.model.dto.request.UserUpdateRequest;
import com.trail.Cadastro.model.dto.response.UserDTO;
import com.trail.Cadastro.service.UserService;
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
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDTO create(@RequestBody @Valid UserCreateRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    public UserDTO update(@RequestBody @Valid UserUpdateRequest request,
                          @PathVariable String id) {
        return userService.update(request, id);
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable String id) {
        return userService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }
}
