package com.trail.Cadastro.entity;

import com.trail.Cadastro.model.enums.StatusCadastro;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    private @Builder.Default StatusCadastro status = StatusCadastro.PENDENTE;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @ManyToMany
    @Builder.Default
    private Set<Usuario> amigos = new HashSet<>();
}
