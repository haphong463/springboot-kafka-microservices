FROM openjdk:17

WORKDIR /app

COPY target/payment-service.jar payment-service.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "payment-service.jar"]