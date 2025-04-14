# # Use JDK 17 as the base image
# FROM openjdk:17-jdk-slim
#
# # Set the working directory inside the container
# WORKDIR /usr/src/app
#
# # Copy the JAR file into the container
# COPY build/libs/*.jar app.jar
#
# # Expose the application port
# EXPOSE 8080
#
# # Run the application
# CMD ["java", "-jar", "app.jar"]
# Build stage
FROM gradle:7-jdk17 as build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
