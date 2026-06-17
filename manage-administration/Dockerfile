# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom files
COPY pom.xml .
COPY manage-administration/pom.xml ./manage-administration/
COPY manage-administration/domain/pom.xml ./manage-administration/domain/
COPY manage-administration/application/pom.xml ./manage-administration/application/
COPY manage-administration/infrastructure/pom.xml ./manage-administration/infrastructure/
COPY manage-administration/infrastructure/infrastructure-repository-psql/pom.xml ./manage-administration/infrastructure/infrastructure-repository-psql/
COPY manage-administration/rest-endpoints/pom.xml ./manage-administration/rest-endpoints/
COPY manage-administration/rest-endpoints/boot/pom.xml ./manage-administration/rest-endpoints/boot/
COPY manage-administration/rest-endpoints/locations-rest-endpoints/pom.xml ./manage-administration/rest-endpoints/locations-rest-endpoints/
COPY manage-administration/rest-endpoints/lockers-rest-endpoints/pom.xml ./manage-administration/rest-endpoints/lockers-rest-endpoints/
COPY manage-administration/rest-endpoints/racks-rest-endpoints/pom.xml ./manage-administration/rest-endpoints/racks-rest-endpoints/
COPY manage-administration/rest-endpoints/users-rest-endpoints/pom.xml ./manage-administration/rest-endpoints/users-rest-endpoints/

# Copy source code
COPY manage-administration ./manage-administration

# Build
RUN mvn clean package -DskipTests -pl manage-administration/rest-endpoints/boot -am

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Locki Project"
LABEL description="Locki Administration Service"

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy JAR from builder
COPY --from=builder /build/manage-administration/rest-endpoints/boot/target/boot-0.0.1-SNAPSHOT.jar app.jar

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

