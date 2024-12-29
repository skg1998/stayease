# Stage 1: Build the application
FROM maven:3.8.8-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the project files
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/api-0.0.1-SNAPSHOT.jar ./app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
