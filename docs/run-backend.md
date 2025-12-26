# Run Backend

## Prerequisites
- Java 17
- Maven 3.9.9
- MySQL 8

## Database
Ensure the schema in `mysql struct.md` has been applied to MySQL (database: `shopdb`).

## Configure
Environment variables (optional):
- `DB_URL` (default: `jdbc:mysql://localhost:3306/shopdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: `root`)
- `JWT_SECRET` (default: `dev-secret-change-me-dev-secret-change-me`)
- `JWT_EXPIRE_MINUTES` (default: `120`)

## Run
```bash
cd backend
mvn spring-boot:run
```

Backend listens on `http://localhost:8080`.
