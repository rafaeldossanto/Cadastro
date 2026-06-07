package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.ConfirmacaoEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmacaoEmailRepository extends JpaRepository<ConfirmacaoEmail, String> {
    Optional<ConfirmacaoEmail> findByToken(String token);
    void deleteByUsuarioId(String usuarioId);
}