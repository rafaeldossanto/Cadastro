package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, String> {
    Optional<EmailConfirmation> findByToken(String token);
    void deleteByUserId(String userId);
}
