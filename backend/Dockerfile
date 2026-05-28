# =============================================================================
# Backend Dockerfile
# Build: gradle bootJar
# Run: java -jar app.jar
# =============================================================================

FROM azul/zulu-openjdk:25-jre AS base

FROM gradle:9.3-jdk25 AS builder
WORKDIR /build
COPY --chown=gradle:gradle . .
RUN ./gradlew bootJar --no-daemon -x test

FROM azul/zulu-openjdk:25-jre AS runtime
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
