package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    Usuario findByEmail(String email);

    @Query("SELECT COUNT(u) + 1 FROM Usuario u")
    Long proximaSequencia();
}