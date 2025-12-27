# Use a lightweight Java image
FROM openjdk:21-jdk-slim
# Set working directory
WORKDIR /app
# Copy application JAR file into the container
COPY target/SalesSavvy-0.0.1-SNAPSHOT.jar app.jar
# Expose the application port
EXPOSE 9090
# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]