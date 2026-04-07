# Stage 1 — Build
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY ims-common/ ./ims-common/
COPY ims-logging-starter/ ./ims-logging-starter/
COPY ims-user-service/ ./ims-user-service/
COPY ims-warehouse-service/ ./ims-warehouse-service/
COPY ims-market-service/ ./ims-market-service/
COPY ims-transfer-service/ ./ims-transfer-service/
COPY ims-scheduling-service/ ./ims-scheduling-service/
COPY ims-reporting-service/ ./ims-reporting-service/
COPY ims-transaction-service/ ./ims-transaction-service/
COPY ims-notification-service/ ./ims-notification-service/
COPY api-gateway/ ./api-gateway/
RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl ims-scheduling-service -am package -DskipTests

# Stage 2 — Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/ims-scheduling-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
