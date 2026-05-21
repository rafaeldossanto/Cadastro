package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.mapper.UsuarioMapper;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository){
        this.repository = repository;
    }

    public UsuarioDTO create(UsuarioCreateRequest request) {
        try {
            Usuario find = repository.findByEmail(request.email());
            if (nonNull(find)) throw new IllegalArgumentException("Conta com esse email ja existente");

            Usuario usuario = UsuarioMapper.toEntity(request);
            repository.save(usuario);
            return UsuarioMapper.toResponse(usuario);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


}
