# Stage 1: Build
FROM gradle:7.6.0-jdk17 AS build

COPY . /appbuild
WORKDIR /appbuild

# Clean and build the application
RUN gradle clean build

# Stage 2: Runtime
FROM openjdk:17-slim

# Set application user
ENV APPLICATION_USER 1033
RUN useradd -ms /bin/bash $APPLICATION_USER

# Copy the built JAR file and resources from the build stage
COPY --from=build /appbuild/build/libs/smartAttendance-all.jar app.jar
COPY --from=build /appbuild/src/main/resources/ /app/resources/

# Set permissions for the application directory
RUN chown -R $APPLICATION_USER /app
RUN chmod -R 755 /app

# Switch to the application user
USER $APPLICATION_USER

WORKDIR /app
