package com.trail.Cadastro.service;

import com.trail.Cadastro.entity.Usuario;
import com.trail.Cadastro.mapper.UsuarioMapper;
import com.trail.Cadastro.model.dto.request.UsuarioCreateRequest;
import com.trail.Cadastro.model.dto.request.UsuarioUpdateReuqest;
import com.trail.Cadastro.model.dto.response.UsuarioDTO;
import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
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
            log.info("Iniciando processo de cadastro de usuario");
            Usuario find = repository.findByEmail(request.email());
            if (nonNull(find)) throw new IllegalArgumentException("Conta com esse email ja existente");

            Usuario usuario = UsuarioMapper.toEntity(request);
            repository.save(usuario);
            log.info("Processo finalizado com sucesso");
            return UsuarioMapper.toResponse(usuario);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public UsuarioDTO update(UsuarioUpdateReuqest request, String id){
        try {
            log.info("Iniciando processo de atualizacao de usuario");
            Usuario usuario = repository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));

            if (isNull(request)) throw new IllegalArgumentException("O campo precisa ser preenchido");

            if (isNull(request.nome())) {
                Usuario find = repository.findByEmail(request.email());
                if (nonNull(find)) throw new IllegalArgumentException("Conta com esse email ja existente");
                usuario.setEmail(request.email());
            }
            if (isNull(request.email())) {
                usuario.setNome(request.nome());
            }
            log.info("Usuario atualizado com sucesso");
            return UsuarioMapper.toResponse(repository.save(usuario));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public UsuarioDTO getById(String id){
        try {
            log.info("Iniciando processo de consulta de usuario");
            return UsuarioMapper.toResponse(repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado")));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void delete(String Id) {
        try {
            log.info("Iniciando processo de desativacao de usuario");
            Usuario usuario = repository.findById(Id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado"));
            usuario.setStatus(StatusCadastro.INATIVO);
            repository.save(usuario);
            log.info("Delecao de usuario bem sucedida");
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
