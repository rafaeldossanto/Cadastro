package com.trail.Cadastro.entity;

import com.trail.Cadastro.model.enums.ProvedorAuth;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(
        name = "conta_vinculada",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provedor", "provedor_usuario_id"}),
        indexes = @Index(name = "idx_conta_vinculada_usuario", columnList = "usuario_id")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaVinculada {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProvedorAuth provedor;

    @Column(name = "provedor_usuario_id")
    private String provedorUsuarioId;

    private String email;

    @Column(nullable = false)
    private LocalDateTime vinculadoEm;
}
