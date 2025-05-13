FROM eclipse-temurin:24-jdk-alpine

WORKDIR /app

COPY build/libs/recruitment-task-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]