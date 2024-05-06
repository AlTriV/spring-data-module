FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY /target/*.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]