# ------------ Build stage ------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy POM first to leverage layer caching for dependencies
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -DskipTests package

# ------------ Runtime stage ------------
FROM eclipse-temurin:21-jre-alpine AS runtime

# Create non-root user
RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

# Copy built jar from build stage
COPY --from=build /app/target/file-manager-*.jar ./app.jar

# Minimal JVM ergonomics for containers
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8082


USER app
ENTRYPOINT ["java","-jar","/app/app.jar"]
