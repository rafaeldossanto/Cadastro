-- Roda DEPOIS do Hibernate criar as tabelas (defer-datasource-initialization),
-- em todo startup, de forma idempotente. Garante a sequence do codigoUsuario
-- num banco novo independente do Flyway (que e pulado quando a imagem PostGIS
-- ja deixa o schema nao-vazio e dispara o baseline).
CREATE SEQUENCE IF NOT EXISTS usuario_codigo_seq START WITH 1 INCREMENT BY 1;
