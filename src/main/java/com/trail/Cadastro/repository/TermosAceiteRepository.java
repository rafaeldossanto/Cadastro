package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.TermosAceite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermosAceiteRepository extends JpaRepository<TermosAceite, String> {
    void deleteByUsuarioId(String usuarioId);
}