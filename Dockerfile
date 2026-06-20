# syntax=docker/dockerfile:1

# ---- Build stage: compile, test, and package the fat jar ----
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Resolve dependencies first so this layer is cached when only sources change.
COPY pom.xml .
RUN mvn -B -e dependency:go-offline

# Build and run tests, then package the executable jar.
COPY src ./src
RUN mvn -B clean package

# ---- Runtime stage: small JRE image that just runs the jar ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Logback writes game events here (see src/main/resources/logback.xml).
ENV UNO_LOG_DIR=/app/logs
# Game history (H2 database) is written under /app/data. Mount a volume there
# (e.g. -v "$PWD/data:/app/data") to keep history across container runs.
RUN mkdir -p /app/logs /app/data

COPY --from=build /app/target/midterm-uno-cli.jar app.jar

# Default to a deterministic bot-only game; override args at `docker run`.
ENTRYPOINT ["java", "-Duno.log.dir=/app/logs", "-jar", "app.jar"]
CMD ["--bots", "3", "--games", "1"]
