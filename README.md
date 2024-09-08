![E-commerce](https://github.com/user-attachments/assets/971fbd4f-0504-4135-9652-0e97e7a4eaf0)
![E-Commerce](https://github.com/user-attachments/assets/292f8933-3c8a-4e17-86f5-482615bfd106)

# E-commerce Microservices Backend Project (BASIC) 🚀

## Introduction 🌟

The **E-commerce Microservices Project** is built using microservice architecture, designed to manage key services such as API Gateway, Service Registry, Product, Order, Stock, Identity, and Payment. The system is powered by modern technologies like Kafka for messaging, Open Feign for inter-service communication, and MySQL for data storage.

## Microservices Architecture 🛠️

### 1. API Gateway 🌐
- **Description**: Acts as the single entry point to route requests to the correct services.
- **Technology**: Spring Cloud Gateway
- **Features**: Handles security, routing, and load balancing.

### 2. Service Registry 📜
- **Description**: Ensures that services can dynamically discover and register with each other.
- **Technology**: Eureka Server (Spring Cloud Netflix)
- **Features**: Service discovery and registry management.

### 3. Product Service 🛒
- **Description**: Manages the product catalog and product details.
- **Technology**: Spring Boot, MySQL
- **Features**: CRUD operations for product data.

### 4. Order Service 🧾
- **Description**: Handles order management for users.
- **Technology**: Spring Boot, MySQL
- **Features**: Order creation, processing, and order history management.

### 5. Stock Service 📦
- **Description**: Manages inventory and stock levels for products.
- **Technology**: Spring Boot, MySQL
- **Features**: Stock tracking, updates, and adjustments.

### 6. Identity Service 🧑‍💻
- **Description**: Provides user authentication and identity management.
- **Technology**: Spring Boot, MySQL
- **Features**: User registration, login, and authentication.

### 7. Payment Service 💳
- **Description**: Manages payment transactions for orders.
- **Technology**: Spring Boot, MySQL
- **Features**: Payment processing, transaction history, and billing.

## Technologies Used 🔧

- **Spring Boot**: Core framework for developing microservices.
- **Apache Kafka**: For asynchronous inter-service communication.
- **Open Feign**: Simplifies HTTP calls between services.
- **MySQL**: Relational database management system for storing service data.
- **Spring Cloud**: Frameworks and tools to manage distributed systems (Eureka, Gateway, etc.).

## Project Setup 🛠️

### Requirements
- **JDK 17** or higher ☕
- **MySQL Server** 🗃️
- **Apache Kafka** for message streaming.

### Running the Microservices 🚀

Instead of running each service individually, you can use the `start.bat` script to launch all services simultaneously:

1. **Configure MySQL**: Ensure that MySQL is running and configure the `application.properties` files for each service with the correct MySQL connection details.
2. **Configure Kafka**: Make sure Kafka is up and running, and update each service’s `application.properties` or `application.yml` files with Kafka configuration details.
3. **Start All Services**:
   - Navigate to the root of the project where the `start.bat` file is located.
   - Run the script by double-clicking `start.bat`. This will start all services at once.

### Configuration ⚙️

- **MySQL Configuration**: Set the database connection details for each service in their `application.properties` files.
- **Kafka Configuration**: Provide Kafka broker details in the `application.properties` or `application.yml` files of each service.

## API Endpoints and Testing 🔍

- **API Gateway**: `http://localhost:9191` serves as the main entry point to interact with all microservices.
- Use tools like **Postman** or **cURL** to test the API endpoints and services.

## Useful Documentation 📚

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

## License 📝

This project is licensed under the [MIT License](LICENSE).

