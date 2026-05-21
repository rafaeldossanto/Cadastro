package com.trail.Cadastro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermosAceite {

    private @Id String id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private Boolean aceito;
    private @Version String versao;
    private LocalDateTime dataAceite;
}
