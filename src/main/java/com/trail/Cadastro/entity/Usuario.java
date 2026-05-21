package com.trail.Cadastro.entity;

import com.trail.Cadastro.model.enums.StatusCadastro;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    private @Id Long id;
    private String nome;
    private String email;
    private String senha;
    private String codigoUsuario;
    private StatusCadastro status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
