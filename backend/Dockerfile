# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY ims-domain/pom.xml ims-domain/
COPY ims-application/pom.xml ims-application/
COPY ims-infrastructure/pom.xml ims-infrastructure/
COPY ims-api/pom.xml ims-api/
COPY ims-bootstrap/pom.xml ims-bootstrap/

# Download dependencies (leverage Docker layer caching)
RUN apk add --no-cache maven && \
    mvn dependency:go-offline -B --no-transfer-progress || true

COPY ims-domain/src ims-domain/src
COPY ims-application/src ims-application/src
COPY ims-infrastructure/src ims-infrastructure/src
COPY ims-api/src ims-api/src
COPY ims-bootstrap/src ims-bootstrap/src

RUN mvn clean package -DskipTests -B --no-transfer-progress

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/ims-bootstrap/target/ims-bootstrap-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
