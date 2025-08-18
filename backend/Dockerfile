# ===== build =====
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
# melhora cache
COPY mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw
COPY pom.xml .
RUN ./mvnw -q -DskipTests dependency:go-offline
# copia fontes e empacota
COPY src src
RUN ./mvnw -q clean package -DskipTests

# ===== run =====
FROM eclipse-temurin:21-jre-alpine
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
WORKDIR /opt/app
RUN adduser -D appuser
USER appuser
COPY --from=builder /app/target/*.jar /opt/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app/app.jar"]
