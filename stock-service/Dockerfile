FROM openjdk:17

WORKDIR /app

COPY target/stock-service.jar stock-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "stock-service.jar"]