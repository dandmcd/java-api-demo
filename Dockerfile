# Step 1: Use an official Maven image to build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Step 2: Set the working directory in the container
WORKDIR /app

# Step 3: Copy the pom.xml and source code into the container
COPY pom.xml .
COPY src ./src

# Step 4: Build the application (this creates the .jar file in the target directory)
RUN mvn clean install

# Step 5: Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Step 6: Set the working directory in the container
WORKDIR /app

# Step 7: Copy the built .jar file from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Step 8: Expose port 8080
EXPOSE 8080

# Step 9: Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
