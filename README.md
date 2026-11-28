# vet-pokemon-backend

Backend REST de la veterinaria Huellitas (Pokemon), proyecto evaluado para Fullstack 2.

Integrantes: Rayen Beltrán, Matías Oviedo, Thomas Mery

Stack: Java 21 · Spring Boot · JPA/Hibernate · PostgreSQL (Neon) · Swagger · HATEOAS

## Ejecutar en local

1. Dentro de `vet-backend/`, copiar `.env.example` como `.env` y completar los datos de Neon.
2. Desde `vet-backend/`: `./mvnw spring-boot:run` (en Windows: `mvnw.cmd spring-boot:run`).
3. Swagger: http://localhost:8080/swagger-ui/index.html
   Usuarios: http://localhost:8080/api/v1/usuarios

Al primer arranque se crean las tablas y se cargan datos de ejemplo (`DatosIniciales`).

## Probar el contenedor en local

```
docker compose up --build
```

Levanta un Postgres local y la API en http://localhost:8080.

## Desplegar en Render

1. New → Web Service → conectar el repositorio de GitHub.
2. Language: **Docker** · Root Directory: `vet-backend` (ahí está el Dockerfile).
3. Environment Variables:
   - `DB_URL` = `jdbc:postgresql://<host-de-neon>/neondb?sslmode=require`
   - `DB_USERNAME` y `DB_PASSWORD` = los de Neon
   - `CORS_ORIGINS` = URL del frontend en Vercel (varias separadas por coma)
4. (Opcional) Health Check Path: `/v3/api-docs`
