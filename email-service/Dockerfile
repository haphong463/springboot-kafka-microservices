FROM openjdk:17

WORKDIR /app

COPY target/email-service.jar email-service.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "email-service.jar"]