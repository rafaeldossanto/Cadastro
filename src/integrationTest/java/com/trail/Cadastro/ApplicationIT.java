package com.trail.Cadastro;

import io.camunda.zeebe.client.ZeebeClient;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
 *
 * Desligar o autostart, porem, tambem remove o bean {@code ZeebeClient} — e
 * RegistrationService/SocialAuthenticationService o recebem no construtor, o que
 * derrubava o contexto com NoSuchBeanDefinitionException. O mock abaixo satisfaz
 * a injecao sem abrir conexao com broker nenhum.
 */
@Tag("integracao")
@SpringBootTest
// Profile de desenvolvimento explicito: sem ele o JwtKeyConfig aborta o boot por
// falta de JWT_RSA_PRIVATE_KEY_PATH (fail closed), que e exatamente o
// comportamento desejado em qualquer ambiente que nao seja dev/test.
@ActiveProfiles("test")
@Testcontainers
class ApplicationIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("trilha_cadastro");

    @MockitoBean
    private ZeebeClient zeebeClient;

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
