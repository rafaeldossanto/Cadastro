package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.ContaVinculada;
import com.trail.Cadastro.model.enums.ProvedorAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContaVinculadaRepository extends JpaRepository<ContaVinculada, String> {

    Optional<ContaVinculada> findByProvedorAndProvedorUsuarioId(ProvedorAuth provedor, String provedorUsuarioId);

    List<ContaVinculada> findByUsuarioId(String usuarioId);

    boolean existsByUsuarioIdAndProvedor(String usuarioId, ProvedorAuth provedor);
}
