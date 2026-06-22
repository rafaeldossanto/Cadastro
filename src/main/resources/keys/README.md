# Chaves RSA — Cadastro

## dev-private-key.pem

Par RSA 2048-bit em formato PKCS#8, **exclusivo para desenvolvimento local**.
Este arquivo está intencionalmente versionado no repositório — **não é um segredo real**.
Em produção, não use este arquivo; injete a chave via `JWT_RSA_PRIVATE_KEY_PATH`.

## Produção

1. Gere um par RSA seguro (ex.: `openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out prod-key.pem`)
2. Armazene em um Secret Manager (AWS Secrets Manager, GCP Secret Manager, Kubernetes Secret, etc.)
3. Monte o arquivo no container e defina `JWT_RSA_PRIVATE_KEY_PATH=/caminho/para/prod-key.pem`

O `kid` é derivado do SHA-256 da chave pública e permanece estável entre restarts —
desde que a mesma chave seja usada, tokens em circulação continuam válidos após deploys.
