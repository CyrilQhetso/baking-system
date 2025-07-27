# Use a base JDK image
FROM eclipse-temurin:17-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR from your machine into the container
COPY target/banking-system-1.0.0.jar app.jar

# Expose the port your Spring Boot app uses
EXPOSE 8080

# Command to run your app
ENTRYPOINT ["java", "-jar", "app.jar"]
