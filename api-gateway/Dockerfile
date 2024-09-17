FROM openjdk:17

WORKDIR /app

COPY target/api-gateway.jar api-gateway.jar

EXPOSE 9191

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]