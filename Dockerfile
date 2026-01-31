# Build stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY --chmod=755 mvnw ./
COPY .mvn/ .mvn/
COPY pom.xml ./
COPY lifegraf-common/ ./lifegraf-common/
COPY lifegraf-collector/ ./lifegraf-collector/
COPY lifegraf-boot/ ./lifegraf-boot/

# Build
RUN ./mvnw package -DskipTests -q

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="kiroe.cn@gmail.com"
LABEL description="LifeGraf - Personal Data Aggregation Platform"

# Set timezone
ENV TZ=Asia/Shanghai

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1000 app && adduser -u 1000 -G app -s /bin/sh -D app

# Copy the built artifact
COPY --from=builder --chmod=755 /app/lifegraf-boot/target/*.jar app.jar

# Create config directory
RUN mkdir -p /app/config && chown -R app:app /app

# Switch to non-root user
USER app

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
