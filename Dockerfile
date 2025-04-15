# # Use JDK 17 as the base image
FROM openjdk:17-jdk-slim
#
# # Set the working directory inside the container
WORKDIR /usr/src/app
#
# # Copy the JAR file into the container
COPY build/libs/*.jar app.jar
#
# # Expose the application port
EXPOSE 8080
#
# # Run the application
CMD ["java", "-jar", "app.jar"]
# FROM openjdk:17-jdk-slim

# WORKDIR /usr/src/app

# Copy dependencies (if using a flat directory approach)
# COPY build/dependencies/ ./lib/
# Copy your thin JAR
# COPY build/libs/*-thin.jar app.jar

# EXPOSE 8080

# Run with dependencies in the classpath
# CMD ["java", "-cp", "app.jar:lib/*", "application.ApplicationKt"]
