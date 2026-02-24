# Stage 1: Build the application
FROM maven:3.9.12-eclipse-temurin-25-noble AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code and build the package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:25.0.2_10-jre-noble
WORKDIR /app

# Copy only the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]