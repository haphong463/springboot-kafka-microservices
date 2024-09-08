# Spring Boot Microservices Project

## Introduction

The Spring Boot Microservices Project is a distributed system designed to manage services such as API Gateway, Service Registry, Product, Order, Stock, Identity, and Payment. The project utilizes modern technologies such as Kafka for messaging, Open Feign for inter-service communication, and MySQL for data storage.

## Services

### 1. API Gateway
- **Description**: Provides a single entry point for all other services.
- **Technology**: Spring Cloud Gateway
- **Functionality**: Routes requests to the appropriate services, manages security, and provides load balancing.

### 2. Service Registry
- **Description**: Manages and helps services discover each other.
- **Technology**: Eureka Server (Spring Cloud Netflix)
- **Functionality**: Registers and discovers services.

### 3. Product Service
- **Description**: Manages product information.
- **Technology**: Spring Boot, MySQL
- **Functionality**: Performs CRUD operations on products.

### 4. Order Service
- **Description**: Manages user orders.
- **Technology**: Spring Boot, MySQL
- **Functionality**: Processes orders, stores, and queries order information.

### 5. Stock Service
- **Description**: Manages the stock of products.
- **Technology**: Spring Boot, MySQL
- **Functionality**: Tracks stock levels and updates on changes.

### 6. Identity Service
- **Description**: Handles user information and authentication.
- **Technology**: Spring Boot, MySQL
- **Functionality**: Manages user registration, login, and user management.

### 7. Payment Service
- **Description**: Handles payments for orders.
- **Technology**: Spring Boot, MySQL
- **Functionality**: Processes payments and manages transactions.

## Technologies Used

- **Spring Boot**: Core framework for building the services.
- **Kafka**: Messaging system for asynchronous communication between services.
- **Open Feign**: HTTP client for service-to-service communication.
- **MySQL**: Relational database for data storage.
- **Spring Cloud**: Tools for managing distributed services (Eureka, Gateway).

## Setup

### Requirements
- JDK 17 or higher
- MySQL Server

### Running Services

Instead of starting each service individually, you can use the `start.bat` file to launch all services at once. Follow these steps:

1. **Configure MySQL**: Ensure that your MySQL Server is running and properly configured. Update the `application.properties` files in each service directory with your MySQL connection details.

2. **Configure Kafka**: Ensure that Kafka is running and properly configured. Update the `application.properties` or `application.yml` files in each service directory with your Kafka connection details.

3. **Run All Services**:
   - Navigate to the root directory of the project (where `start.bat` is located).
   - Double-click `start.bat` to start all services simultaneously.

### Configuration

- **MySQL**: Configure the connection in the `application.properties` file for each service.
- **Kafka**: Configure Kafka connection in the `application.properties` or `application.yml`.

## Testing

- Access the API Gateway at `http://localhost:8080` to interact with the services.
- Use tools like Postman to make HTTP requests to the service endpoints.

## Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Apache Kafka Documentation](https://kafka.apache.org/documentation/)

## License

This project is licensed under the [MIT License](LICENSE).
