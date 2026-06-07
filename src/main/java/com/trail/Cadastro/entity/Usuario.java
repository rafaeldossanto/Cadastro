package com.trail.Cadastro.entity;

import com.trail.Cadastro.model.enums.StatusCadastro;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario", indexes = {
        @Index(name = "idx_usuario_codigo", columnList = "codigoUsuario"),
        @Index(name = "idx_usuario_email", columnList = "email")
})
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
}
