package com.trail.Cadastro.repository;

import com.trail.Cadastro.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByEmail(String email);

    @Query(value = "SELECT nextval('usuario_codigo_seq')", nativeQuery = true)
    Long nextSequence();
}
