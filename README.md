![image](https://github.com/user-attachments/assets/e56d8eb1-0fd1-4999-8597-b2100f93b712)

# E-commerce Backend Microservices Project (Dockerized) 🚀

## Introduction 🌟

Welcome to the **E-commerce Backend Microservices Project**! This project uses a microservices architecture to provide a scalable and maintainable e-commerce platform. Utilizing Docker and Docker Compose, developers can easily set up and manage the entire backend ecosystem, including services such as API Gateway, Service Registry (Eureka Server), Product, Order, Stock, Identity, and Payment. Modern technologies like Kafka for messaging, Open Feign for inter-service communication, and MySQL for data storage create a powerful and efficient system.

## Microservices Architecture 🛠️

The project is divided into several interconnected microservices, each handling specific business functions. Here's an overview of each service:

### 1. API Gateway 🌐
- **Description**: The single entry point for all client requests, routing them to the appropriate microservices.
- **Technology**: Spring Cloud Gateway
- **Features**: Security handling, routing, load balancing, and rate limiting.

### 2. Service Registry 📜
- **Description**: Supports the dynamic discovery and registration of microservices within the ecosystem.
- **Technology**: Eureka Server (Spring Cloud Netflix)
- **Features**: Service discovery, registration management, and health monitoring.

### 3. Product Service 🛒
- **Description**: Manages the product catalog, including product details and inventory.
- **Technology**: Spring Boot, MySQL
- **Features**: CRUD operations for product data, inventory management.

### 4. Order Service 🧾
- **Description**: Oversees order processing, tracking, and history for users.
- **Technology**: Spring Boot, MySQL
- **Features**: Order creation, processing, status tracking, and history management.

### 5. Stock Service 📦
- **Description**: Manages inventory levels and adjusts stock for products.
- **Technology**: Spring Boot, MySQL
- **Features**: Inventory tracking, updates, and automatic restocking triggers.

### 6. Identity Service 🧑‍💻
- **Description**: Handles user authentication, authorization, and identity management.
- **Technology**: Spring Boot, MySQL
- **Features**: User registration, login, role management, and authentication tokens.

### 7. Payment Service 💳
- **Description**: Manages payment processing, transactions, and invoices.
- **Technology**: Spring Boot, MySQL
- **Features**: Payment processing, transaction history, and invoice management.

## Technologies Used 🔧

- **Spring Boot**: The main framework for developing microservices.
- **Apache Kafka**: Supports inter-service communication via asynchronous messaging.
- **Open Feign**: Simplifies HTTP calls between microservices with declarative REST clients.
- **MySQL**: Relational database management system for storing service-specific data.
- **Spring Cloud**: Provides tools for managing a distributed system (Eureka, Gateway, etc.).
- **Docker**: Containerizes each microservice to ensure consistent and isolated environments.
- **Docker Compose**: Coordinates multi-container Docker applications, managing service dependencies and networking.

## Project Setup 🛠️

### 🛠️ **Requirements**

Ensure you have the following installed on your development machine:

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: [Install Docker Compose](https://docs.docker.com/compose/install/)
- **Git**: To clone the repository.
- **Web Browser**: To access the service dashboard and APIs.

### 🚀 **Clone Repository**

```bash
git clone https://github.com/haphong463/springboot-kafka-microservices.git
cd springboot-kafka-microservices
```

### 🐳 Run Microservices With Docker Compose

The project uses Docker Compose to manage all microservices and their dependencies. Follow these steps to boot up the system:

#### Ensure Docker and Docker Compose Are Running

Ensure Docker Desktop (or Docker Engine) is running on your machine.

#### Build and Start All Services

From the project root directory, execute:

```bash
docker-compose up -d
```

**Flag:**
- `-d`: Run containers in detached mode.

#### Check All Services Are Running

Check the status of all running containers:

```bash
docker-compose ps
```

**Expected Result:**

```
    Name                      Command               State               Ports
--------------------------------------------------------------------------------------------
api-gateway            java -jar /app.jar            Up      0.0.0.0:9191->9191/tcp
eureka-server          java -jar /app.jar            Up      0.0.0.0:8761->8761/tcp
identity-service       java -jar /app.jar            Up      0.0.0.0:9898->9898/tcp
kafka                  /etc/confluent/docker/run   Up      0.0.0.0:9092->9092/tcp, 0.0.0.0:29092->29092/tcp
mysql-order-service    docker-entrypoint.sh mysqld   Up      0.0.0.0:3307->3306/tcp
mysql-identity-service docker-entrypoint.sh mysqld   Up      0.0.0.0:3308->3306/tcp
mysql-payment-service  docker-entrypoint.sh mysqld   Up      0.0.0.0:3309->3306/tcp
mysql-product-service  docker-entrypoint.sh mysqld   Up      0.0.0.0:3310->3306/tcp
mysql-stock-service    docker-entrypoint.sh mysqld   Up      0.0.0.0:3311->3306/tcp
order-service          java -jar /app.jar            Up      0.0.0.0:8080->8080/tcp
payment-service        java -jar /app.jar            Up      0.0.0.0:8085->8085/tcp
product-service        java -jar /app.jar            Up      0.0.0.0:8084->8084/tcp
stock-service          java -jar /app.jar            Up      0.0.0.0:8081->8081/tcp
zookeeper              /etc/confluent/docker/run   Up      0.0.0.0:2181->2181/tcp
```

### 🛠️ Service Configuration

All configurations are managed via environment variables defined in the `docker-compose.yml` file. However, if you need to customize configurations, you can edit the `application-docker.properties` or `application.yml` files in each microservice.

#### Example: Configuring order-service in docker-compose.yml

```yaml
order-service:
  image: springboot-kafka-microservices/order-service:latest
  container_name: order-service
  depends_on:
    - kafka
    - mysql-order-service
    - eureka-server
  ports:
    - "8080:8080"
  environment:
    SPRING_DATASOURCE_URL: jdbc:mysql://mysql-order-service:3306/order_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: root
    SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka:9092
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE: http://eureka-server:8761/eureka/
    SPRING_PROFILES_ACTIVE: docker
  networks:
    - shop-network
```

- **SPRING_DATASOURCE_URL**: Connects to the `mysql-order-service` database.
- **SPRING_KAFKA_BOOTSTRAP_SERVERS**: Specifies the Kafka broker within Docker.
- **EUREKA_CLIENT_SERVICEURL_DEFAULTZONE**: Registers with the Eureka Server.

## API Endpoints and Testing 🔍

### 🛠️ Accessing Services

- **Eureka Server Dashboard**: [http://localhost:8761](http://localhost:8761)
  - Monitor registered services and their statuses.
  
- **API Gateway**: [http://localhost:9191](http://localhost:9191)
  - The entry point for all API requests.
  
- **Order Service**: [http://localhost:8080](http://localhost:8080)

- **Payment Service**: [http://localhost:8085](http://localhost:8085)

- **Product Service**: [http://localhost:8084](http://localhost:8084)

- **Stock Service**: [http://localhost:8081](http://localhost:8081)

- **Identity Service**: [http://localhost:9898](http://localhost:9898)

### 🛠️ Testing APIs

Use tools like Postman, Insomnia, or cURL to interact with the APIs. Below are examples of how to perform basic operations, considering the role-based access for each operation and using cookies for authentication:

### 🛠️ User Registration and Authentication

Below are the steps and examples for registering a new user and obtaining authentication tokens using cookies.

#### 🔐 User Registration

To register a new user, provide their name, password, email, and roles. Roles should be specified as an array, and can include roles like `CUSTOMER`, `EMPLOYEE`, etc.

**Endpoint**: `POST http://localhost:9191/api/v1/auth/register`

```bash
curl -X POST http://localhost:9191/api/v1/auth/register \
     -H "Content-Type: application/json" \
     -d '{
           "name": "johndoe",
           "password": "securepassword",
           "email": "john.doe@example.com",
           "roles": ["CUSTOMER"]
         }'
```

#### 🔑 Authentication (Login)

To log in and receive a session cookie, submit your username and password to the authentication endpoint. The server will return a cookie containing your session ID if the login is successful.

**Endpoint**: `POST http://localhost:9191/api/v1/auth/token`

```bash
curl -X POST http://localhost:9191/api/v1/auth/token \
     -H "Content-Type: application/json" \
     -d '{
           "username": "johndoe",
           "password": "securepassword"
         }'
```

Use the session cookie stored in `cookies.txt` for subsequent requests that require authentication. This setup ensures secure handling of user sessions and simplifies credential management across multiple requests.

These additions outline the processes of user registration and login, guiding users on how to interact with your e-commerce platform's authentication system. Let me know if you'd like further enhancements or additional details!


#### 📦 Example of Product Service

**Add New Product (Role Required: EMPLOYEE)**

To perform this operation, your user must be authenticated as an employee. Ensure your cookie with authentication details is included in the request.

```bash
curl -X POST http://localhost:9191/api/v1/products \
     -H "Content-Type: application/json" \
     -b "token=your_jwt_token" \
     -d '{
           "name": "New Product",
           "imageUrl": "image1.png",
           "description": "Product description",
           "price": 99.99,
           "stockQuantity": 100
         }'
```

#### 🧾 Example of Order Service

**Create New Order (Role Required: CUSTOMER)**

To perform this operation, your user must be authenticated as a customer. Ensure your cookie with authentication details is included in the request.

```bash
curl -X POST http://localhost:9191/api/v1/order \
     -H "Content-Type: application/json" \
     -b "token=your_jwt_token" \
     -d '{
            "orderItems": [
                {
                    "productId": 02f6f017-816d-419f-a680-26f814be70e5
                    "quantity": 1
                }
            ],
            "paymentMethod": "COD"
         }'
```

This modification ensures that the authentication method reflects the use of cookies as per your application's configuration. If there are more details to adjust or add, feel free to tell me!

## Database Management 🗃️

Each microservice has a separate MySQL database to ensure data isolation and integrity. Here's how you can manage them:

### 🐳 Accessing MySQL Databases via Docker

#### From Server Using MySQL Client

You can connect to any MySQL database using the host ports mapped in `docker-compose.yml`.

**Example: Connect to `order_db`**

```bash
mysql -h 127.0.0.1 -P 3307 -u root -p
```

- **Host**: `127.0.0.1`
- **Port**: `3307` (mapped to container port `3306`)
- **Username**: `root`
- **Password**: `root`

#### From Inside the Docker Network

Services can communicate with each other using service names and internal ports.

**Example: Access `order_db` from `order-service`**

```bash
mysql -h mysql-order-service -P 3306 -u root -p
```

### 🐳 Using Docker Exec to Access MySQL Inside Container

**Enter MySQL Container**

```bash
docker exec -it mysql-order-service bash
```

**Connect to MySQL**

```bash
mysql -u root -proot order_db
```

- **Username**: `root`
- **Password**: `root`
- **Database**: `order_db`

**Add Record to Role Table**

```sql
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name) VALUES ('EMPLOYEE'), ('ADMINISTRATOR'), ('CUSTOMER');
```

**Check Records Have Been Successfully Added**

```sql
SELECT * FROM roles;
```

**Expected Result:**

```
+----+---------------+
| id | name          |
+----+---------------+
|  1 | EMPLOYEE      |
|  2 | ADMINISTRATOR |
|  3 | CUSTOMER      |
+----+---------------+
```

**Exit MySQL and Container**

```bash
EXIT;
exit
```

## Troubleshooting 🛠️

### ❗ Common Issues and Solutions

#### Eureka Server Not Registering Services

- **Issue**: Services not appearing on Eureka Dashboard.
- **Solution**:
  - Ensure the environment variable `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` is set correctly as `http://eureka-server:8761/eureka/` in `docker-compose.yml`.
  - Check network connectivity between services.
  - Check logs of both the service and Eureka Server for registration errors.

#### Kafka Listener Port Conflict

- **Issue**: `java.lang.IllegalArgumentException: requirement failed: Each listener must have a different port`
- **Solution**:
  - Update `KAFKA_ADVERTISED_LISTENERS` to use different ports for each listener in `docker-compose.yml`.
  
  **Example:**

  ```yaml
  KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
  ```

#### MySQL Volume Not Defined

- **Issue**: Service "mysql-product-service" refers to undefined volume `mysql-product-service-data`
- **Solution**:
  - Define all necessary volumes in the `volumes` section of `docker-compose.yml`.
  
  ```yaml
  volumes:
    mysql-order-service-data:
    mysql-identity-service-data:
    mysql-payment-service-data:
    mysql-product-service-data:
    mysql-stock-service-data:
  ```

#### Service Not Starting

- **Issue**: Service containers crashing or not starting.
- **Solution**:
  - Check logs of the specific service:
  
    ```bash
    docker-compose logs -f <service-name>
    ```
  - Ensure environment variables are set correctly.
  - Check database connections and login details.

#### Port Conflict on Host

- **Issue**: Host ports being used by another service.
- **Solution**:
  - Change host port mappings in `docker-compose.yml` to unused ports.
  
  **Example:**

  ```yaml
  ports:
    - "3306:3306"    # Change to "3312:3306" if 3306 is being used
  ```

### 🔍 Checking Logs

Use Docker Compose to view logs of any service:

```bash
docker-compose logs -f <service-name>
```

**Example: View Logs of `eureka-server`**

```bash
docker-compose logs -f eureka-server
```

### 🐳 Rebuilding Containers

If you make changes to the code or configurations, rebuild and restart the affected services:

```bash
docker-compose up -d --build <service-name>
```

**Example: Rebuild and Restart `order-service`**

```bash
docker-compose up -d --build order-service
```

## Useful Documentation 📚

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)
- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Eureka Server Documentation](https://cloud.spring.io/spring-cloud-netflix/multi/multi__service_discovery_eureka_clients.html)
- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)

## License 📝

This project is licensed under the MIT License.
