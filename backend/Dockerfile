# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim AS app

# Set the working directory inside the container
WORKDIR /app

# Copy the executable JAR file
COPY application/target/*.jar app.jar

# Expose port (adjust according to your app's configuration)
EXPOSE 8080

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]


FROM openjdk:17-jdk-slim AS is

WORKDIR /app

COPY InventoryService/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]