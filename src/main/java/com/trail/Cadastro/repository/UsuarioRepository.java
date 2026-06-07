package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Usuario findByEmail(String email);

    /**
     * Proximo numero de sequencia para compor o codigoUsuario (nome + numero).
     *
     * Usa a sequence nativa do banco (nextval), que e ATOMICA — cada chamada
     * devolve um valor unico mesmo sob cadastros concorrentes. Substitui o
     * antigo COUNT(u)+1, que tinha race condition (dois cadastros simultaneos
     * geravam o mesmo numero, criando codigoUsuario duplicado).
     *
     * A sequence "usuario_codigo_seq" e criada pela migration do Flyway.
     */
    @Query(value = "SELECT nextval('usuario_codigo_seq')", nativeQuery = true)
    Long proximaSequencia();
}
