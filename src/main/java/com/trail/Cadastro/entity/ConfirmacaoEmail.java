package com.trail.Cadastro.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class ConfirmacaoEmail {

    private @Id String id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    private String token;
    private StatusConfirmacao status;
    private LocalDateTime expiraEm;
    private LocalDateTime dataConfirmacao;
}
