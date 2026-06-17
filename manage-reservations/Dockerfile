# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy root pom
COPY pom.xml .

# Copy manage-reservations pom files
COPY manage-reservations/pom.xml ./manage-reservations/
COPY manage-reservations/manage-reservations-domain/pom.xml ./manage-reservations/manage-reservations-domain/
COPY manage-reservations/manage-reservations-application/pom.xml ./manage-reservations/manage-reservations-application/
COPY manage-reservations/manage-reservations-infrastructure/pom.xml ./manage-reservations/manage-reservations-infrastructure/
COPY manage-reservations/manage-reservations-rest-endpoints/pom.xml ./manage-reservations/manage-reservations-rest-endpoints/
COPY manage-reservations/manage-reservations-rest-endpoints/boot/pom.xml ./manage-reservations/manage-reservations-rest-endpoints/boot/

# Copy source code
COPY manage-reservations ./manage-reservations

# Build
RUN mvn clean package -DskipTests -pl manage-reservations/manage-reservations-rest-endpoints/boot -am

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Locki Project"
LABEL description="Locki Reservations Service"

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy JAR from builder
COPY --from=builder /build/manage-reservations/manage-reservations-rest-endpoints/boot/target/boot-0.0.1-SNAPSHOT.jar app.jar

# Create non-root user for security
RUN addgroup -g 1000 locki && adduser -D -u 1000 -G locki locki
USER locki

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]

