# Use an official OpenJDK runtime as a parent image
FROM openjdk:21.0.4

# Set the working directory in the container
WORKDIR /app

# Copy the project’s jar file into the container at /app
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]