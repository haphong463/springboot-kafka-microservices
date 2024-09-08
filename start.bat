@echo off
echo Starting services...

start "Service Registry" cmd /k "cd service-registry && mvnw spring-boot:run"
start "API Gateway" cmd /k "cd api-gateway && mvnw spring-boot:run"
start "Product Service" cmd /k "cd product-service && mvnw spring-boot:run"
start "Order Service" cmd /k "cd order-service && mvnw spring-boot:run"
start "Stock Service" cmd /k "cd stock-service && mvnw spring-boot:run"
start "Identity Service" cmd /k "cd identity-service && mvnw spring-boot:run"
start "Payment Service" cmd /k "cd payment-service && mvnw spring-boot:run"

echo All services have been started.
