package com.trail.Cadastro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmacaoEmail extends JpaRepository<ConfirmacaoEmail,String> {
}
