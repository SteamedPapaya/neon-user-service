# Dockerfile for Backend
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the host to the container
COPY build/libs/tonari-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 to the host
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]