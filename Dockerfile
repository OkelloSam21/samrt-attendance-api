FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew shadowJar --no-daemon

FROM openjdk:17-jre-slim
WORKDIR /app
COPY --from=builder /app/build/libs/smartAttendance-all.jar app.jar
EXPOSE 8080
ENV PORT=8080
CMD ["java", "-jar", "app.jar"]