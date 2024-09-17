FROM openjdk:17

WORKDIR /app

COPY target/order-service.jar order-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "order-service.jar"]