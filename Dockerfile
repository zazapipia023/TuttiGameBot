FROM amazoncorretto:17-alpine

COPY src/main/resources/application.properties /app/application.properties

COPY target/tuttifruttibot-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENV SPRINGCONFIGLOCATION=/app/application.properties

CMD ["java", "-jar", "/app/app.jar"]