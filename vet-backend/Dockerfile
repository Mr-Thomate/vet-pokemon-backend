# ---------- Etapa 1: compilar el proyecto con Maven ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Se copia primero solo el pom.xml para que Docker guarde en caché las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Etapa 2: imagen liviana solo con el .jar ----------
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN adduser --system --group springuser
USER springuser

COPY --from=build /app/target/*.jar app.jar

# Render entrega el puerto en la variable PORT (application.yaml la lee)
EXPOSE 8080

# MaxRAMPercentage evita que la JVM pase el límite de memoria del plan gratis (512 MB)
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
