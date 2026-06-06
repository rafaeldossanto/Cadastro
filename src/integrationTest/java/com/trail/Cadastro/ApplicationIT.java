package com.trail.Cadastro;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Teste de integracao do Cadastro: sobe o contexto Spring completo contra um
 * PostgreSQL real (Testcontainers), validando a camada JPA/web.
 *
 * O cliente Camunda/Zeebe e DESLIGADO no perfil de teste — o contexto nao
 * tenta conectar a um broker, que exigiria infra externa e tornaria o teste
 * lento e fragil. A orquestracao Camunda e validada separadamente (os workers
 * ja tem testes de unidade); aqui o foco e a inicializacao da aplicacao e a
 * persistencia.
 */
@Tag("integracao")
@SpringBootTest
@Testcontainers
class ApplicationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("trilha_cadastro");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Desliga o autostart do cliente Zeebe/Camunda no contexto de teste.
        registry.add("zeebe.client.enabled", () -> "false");
        registry.add("camunda.client.zeebe.enabled", () -> "false");
    }

    @Test
    void contextLoads() {
    }
}
