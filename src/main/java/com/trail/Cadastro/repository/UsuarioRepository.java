package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByEmail(String email);

    /**
     * Proximo numero de sequencia para compor o codigoUsuario (nome + numero).
     *
     * Usa a sequence 