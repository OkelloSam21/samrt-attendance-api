# Stage 1: Build
FROM gradle:7.6.0-jdk17 AS builder
WORKDIR /workspace
COPY --chown=gradle:gradle . .
RUN gradle shadowJar

# Stage 2: Runtime
FROM openjdk:17-slim
WORKDIR /app
COPY --from=builder /workspace/build/libs/smartAttendance-all.jar app.jar
EXPOSE 8080
ENV PORT=8080
CMD ["java", "-jar", "app.jar"]