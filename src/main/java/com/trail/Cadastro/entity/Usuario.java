package com.trail.Cadastro.entity;

import com.trail.Cadastro.model.enums.StatusCadastro;
import com.trail.Cadastro.trace.TraceContext;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_usuario_codigo", columnNames = "codigoUsuario"),
        indexes = @Index(name = "idx_usuario_email", columnList = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    private @Id String id;
    private String nome;
    private String email;
    private String senha;
    private String codigoUsuario;
    private @Builder.Default StatusCadastro status = StatusCadastro.PENDENTE;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @Column(name = "trace_id")
    private String traceId;

    @PrePersist
    void aoCriar() {
        if (traceId == null) {
            traceId = TraceContext.atual();
        }
    }
}
