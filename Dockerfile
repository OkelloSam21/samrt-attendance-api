FROM openjdk:17-slim

WORKDIR /app

COPY build/libs/smartAttendance-all.jar app.jar

EXPOSE 8080
ENV PORT=8080

CMD ["java", "-jar", "app.jar"]