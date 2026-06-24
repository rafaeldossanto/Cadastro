# Cadastro

Serviço de **identidade e autenticação** da Trilha. É o emissor central de tokens: cadastra usuários, valida login social (Google/Apple), emite o *access token* da aplicação (JWT RSA) e publica a chave pública (JWKS) que os demais serviços usam para validar esse token. Orquestra o fluxo de cadastro com Camunda/Zeebe.

- **Porta:** `8080`
- **Pacote raiz:** `com.trail.Cadastro`
- **Banco:** PostgreSQL `trilha_cadastro` (porta `5432`)

## O que faz

- **Cadastro de usuário** com geração de `codigoUsuario` único (`nome + "#" + sequence` atômica via Flyway).
- **Emissor de JWT**: assina o access token da app com chave RSA (carregada de um PEM estável — configure em produção, senão um restart invalida os tokens) e expõe o **JWKS** em `GET /oauth2/jwks`.
- **Login social**: valida o ID token do provedor (Google/Apple) contra o JWKS público deles (issuer + audience/client-id), cria/vincula a conta e devolve o token da app.
- **Dev-login** (`@Profile("dev")`): atalho de login por email/nome, sem provedor social. Não existe em produção.
- **Confirmação de email** por SMTP (`JavaMailSender`) com token de expiração.
- **Orquestração Camunda/Zeebe**: workers para salvar dados, enviar email, liberar conta e deletar dados por timeout.
- Também atua como **resource server** (defense-in-depth): valida o próprio Bearer com a chave pública local.

## Stack

Spring Boot 4.0.6 · Java 21 · Spring Data JPA · OAuth2 Resource Server · Spring Mail · Flyway · Camunda/Zeebe (`spring-boot-starter-camunda` 8.5.0) · Lombok · logs JSON (logstash-logback-encoder).

## Infra (compose.yaml)

| Serviço | Imagem | Porta |
|---|---|---|
| PostgreSQL | `postgres:16` | `5432` |
| Zeebe (Camunda) | `camunda/zeebe:8.5.0` | `26500` |

Em **dev**, `spring-boot-docker-compose` sobe o `compose.yaml` automaticamente ao iniciar a aplicação.

## Como rodar

```bash
# requer JDK 21 (JAVA_HOME apontando para o JDK 21)
export JAVA_HOME=/caminho/para/jdk-21

# variáveis de ambiente esperadas (sem defaults sensíveis em prod):
#   DB_USERNAME, DB_PASSWORD, MAIL_USERNAME, MAIL_PASSWORD,
#   GOOGLE_CLIENT_ID, APPLE_CLIENT_ID, JWT_ISSUER, ...

./gradlew bootRun
```

Perfil de produção: `application-prod` (ative com `SPRING_PROFILES_ACTIVE=prod`) — logging JSON e schema sob controle do Flyway (`DDL_AUTO=validate`).

## Principais endpoints

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/usuario` | cria usuário (público) |
| `PUT`/`GET`/`DELETE` | `/usuario/{id}` | atualiza / consulta / inativa |
| `POST` | `/auth/social` | login social (Google/Apple) |
| `POST` | `/auth/dev-login` | login de desenvolvimento (perfil `dev`) |
| `GET` | `/auth/confirmar-email` | confirma email pelo token |
| `POST` | `/auth/aceitar-termos` | aceita os termos |
| `GET` | `/oauth2/jwks` | chave pública para validação do JWT |

## Testes

```bash
./gradlew test             # unitários (exclui @Tag("integracao"))
./gradlew integrationTest  # integração com Postgres real (Testcontainers)
```

## Convenções

Identificadores do código em **inglês**; **contrato (JSON), rotas e colunas do banco em português** (mantidos via `@JsonProperty`/`@Column`). Claims do JWT (`codigoUsuario`, `email`) e job-types/variáveis do Camunda são parte do contrato e permanecem em PT. Correlação de requisições via header `X-Trace-Id` (no MDC dos logs).

> Parte da arquitetura da Trilha: **Cadastro (8080)** · APP (8081) · loc (8082) · midia (8083) · BFF (8090).
