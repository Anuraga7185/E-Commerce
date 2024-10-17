# Base image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy the packaged application
COPY target/userservice-0.0.1-SNAPSHOT.jar userservice.jar

# Run the application
ENTRYPOINT ["java", "-jar", "userservice.jar"]
