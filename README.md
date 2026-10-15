# User Mail Sender MS

Microsserviços Spring Boot para cadastro de usuários e processamento assíncrono de notificações por e-mail.

## Arquitetura

```text
Cliente HTTP
    |
    v
User Service :8081 ----> PostgreSQL (ms-user-ms)
    |
    | mensagem JSON: email-queue
    v
RabbitMQ
    |
    v
Email Service :8080 ---> PostgreSQL (ms_email_ms)
```

Ao criar um usuário, o `user` persiste os dados e publica uma mensagem na fila `email-queue`. O `email` consome essa mensagem e processa os dados do destinatário, assunto e corpo.

## Serviços e funcionalidades

### User Service

- Cadastro de usuários.
- Validação de nome e e-mail.
- Verificação de e-mail duplicado.
- Listagem de usuários.
- Exclusão por UUID.
- Publicação do evento de criação na fila RabbitMQ.
- Documentação OpenAPI/Swagger.

### Email Service

- Consumo da fila `email-queue`.
- Conversão das mensagens RabbitMQ para JSON.
- Modelo JPA para informações de e-mail.

O consumidor atual registra o endereço recebido no console. O envio SMTP efetivo e a persistência da mensagem consumida ainda não estão conectados ao fluxo atual.

## Stack

- Java 17
- Spring Boot 3.5.0
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring AMQP
- Spring Mail
- Springdoc OpenAPI 2.8.13
- PostgreSQL 16
- RabbitMQ
- Maven
- Lombok
- JUnit e Spring Boot Test
- Docker Compose

## Pré-requisitos

- JDK 17 ou superior.
- Docker e Docker Compose.
- RabbitMQ acessível pelos serviços.
- Maven instalado ou os Maven Wrappers incluídos no projeto.

## Configuração por ambiente

As aplicações utilizam variáveis de ambiente para não manter credenciais nos arquivos `application.yml`.

1. Copie o arquivo de exemplo:

   ```bash
   cp .env.example .env
   ```

   No PowerShell:

   ```powershell
   Copy-Item .env.example .env
   ```

2. Edite `.env` com os valores da sua máquina e do seu RabbitMQ.
3. Nunca versione `.env`. Ele já está no `.gitignore`.

O `.env` local é importado automaticamente quando os serviços são iniciados a partir de suas respectivas pastas usando Maven:

```text
optional:file:../.env[.properties]
```

Também é possível fornecer as mesmas variáveis diretamente no ambiente do processo.

> As credenciais RabbitMQ que estavam versionadas anteriormente devem ser rotacionadas no provedor antes de publicar ou reutilizar o repositório.

### Variáveis disponíveis

| Variável | Uso |
|---|---|
| `USER_DB_URL` | URL JDBC do banco do User Service |
| `USER_DB_USERNAME` | Usuário do banco do User Service |
| `USER_DB_PASSWORD` | Senha do banco do User Service |
| `EMAIL_DB_URL` | URL JDBC do banco do Email Service |
| `EMAIL_DB_USERNAME` | Usuário do banco do Email Service |
| `EMAIL_DB_PASSWORD` | Senha do banco do Email Service |
| `RABBITMQ_ADDRESSES` | Host do RabbitMQ |
| `RABBITMQ_USERNAME` | Usuário do RabbitMQ |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ |
| `RABBITMQ_VIRTUAL_HOST` | Virtual host do RabbitMQ |

## Subindo os bancos

Cada serviço possui um `docker-compose.yml` próprio.

### User Service

```bash
cd user
docker compose up -d
```

Banco em `localhost:5435`, database `ms-user-ms`, usuário `postgres-user` e senha `postgres-password`.

### Email Service

```bash
cd email
docker compose up -d
```

Banco em `localhost:5433`, database `ms_email_ms`, usuário `postgres` e senha `postgres`.

## Executando os serviços

Em um terminal, execute o User Service:

```bash
cd user
./mvnw spring-boot:run
```

No Windows:

```powershell
cd user
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8081`.

Em outro terminal, execute o Email Service:

```bash
cd email
./mvnw spring-boot:run
```

No Windows:

```powershell
cd email
.\mvnw.cmd spring-boot:run
```

O serviço ficará disponível em `http://localhost:8080`.

## API de usuários

### Criar usuário

```http
POST /user/create
Content-Type: application/json
```

```json
{
  "name": "Maria Silva",
  "email": "maria@example.com"
}
```

Exemplo:

```bash
curl -X POST http://localhost:8081/user/create \
  -H "Content-Type: application/json" \
  -d '{"name":"Maria Silva","email":"maria@example.com"}'
```

Resposta de sucesso: `201 Created`.

### Listar usuários

```http
GET /user
```

Exemplo:

```bash
curl http://localhost:8081/user
```

Resposta de sucesso: `200 OK`.

### Excluir usuário

```http
DELETE /user/delete/{id}
```

Respostas:

- `204 No Content`: usuário excluído.
- `404 Not Found`: usuário não encontrado.

## OpenAPI

Com o User Service em execução:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- Especificação: `http://localhost:8081/v3/api-docs`

## Mensagem RabbitMQ

A fila utilizada é:

```text
email-queue
```

Exemplo da mensagem publicada após o cadastro:

```json
{
  "userId": "6f6c2df5-9e6b-4a8a-9e5d-6d0c2f6cbb8a",
  "emailTo": "maria@example.com",
  "emailSubject": "Bem vindo!",
  "body": "Olá Maria Silva, obrigado por se juntar a nós!"
}
```

## Testes

User Service:

```bash
cd user
./mvnw test
```

Email Service:

```bash
cd email
./mvnw test
```

No Windows, substitua `./mvnw` por `.\mvnw.cmd`.

## Build

```bash
cd user
./mvnw clean package

cd ../email
./mvnw clean package
```

Os JARs serão gerados em `user/target/` e `email/target/`.

## Parando os bancos

Em cada pasta de serviço:

```bash
docker compose down
```

Para remover também os volumes e dados persistidos:

```bash
docker compose down -v
```

## Segurança e produção

- Não versione `.env`.
- Rotacione credenciais que tenham sido expostas anteriormente.
- Use um gerenciador de segredos em produção.
- Substitua `ddl-auto: update` por migrations com Flyway ou Liquibase.
- Adicione tratamento global de exceções e health checks.
- Configure envio SMTP real, retries e persistência do resultado do processamento de e-mails.
