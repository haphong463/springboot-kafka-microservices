# Identity Service API Documentation 🧑‍💻

## Base URL: `http://localhost:9191`

## Authentication Endpoints 🛡️

### 1. **User Login**
- **Endpoint**: `/api/v1/auth/login`
- **Method**: POST
- **Description**: Authenticates a user and returns a JWT token.
- **Request Body**:
  ```json
  {
    "username": "string",
    "password": "string"
  }
  ```
- **Response**:
  - **200 OK**: User is successfully authenticated, and JWT token is returned.
  ```json
  {
    "timestamp": "2024-09-08T10:45:21.123Z",
    "data": {
      "token": "jwt-token-string"
    },
    "statusCode": 200
  }
  ```
  - **401 Unauthorized**: Authentication failed (incorrect credentials).
  ```json
  {
    "timestamp": "2024-09-08T10:45:21.123Z",
    "data": {
      "error": "Invalid username or password."
    },
    "statusCode": 401
  }
  ```
- **Example**:
  ```
  curl -X POST http://localhost:9191/api/v1/auth/login -H "Content-Type: application/json" -d '{"username":"user123","password":"password123"}'
  ```

---

### 2. **User Registration**
- **Endpoint**: `/api/v1/auth/register`
- **Method**: POST
- **Description**: Registers a new user with the provided details.
- **Request Body**:
  ```json
  {
    "username": "string",
    "email": "string",
    "password": "string",
    "firstName": "string",
    "lastName": "string"
  }
  ```
- **Response**:
  - **201 Created**: User is successfully registered.
  ```json
  {
    "timestamp": "2024-09-08T10:45:21.123Z",
    "data": {
      "message": "User registered successfully.",
      "user": {
        "id": 1,
        "username": "user123",
        "email": "user123@example.com",
        "firstName": "John",
        "lastName": "Doe"
      }
    },
    "statusCode": 201
  }
  ```
  - **400 Bad Request**: Registration failed (validation error).
  ```json
  {
    "timestamp": "2024-09-08T10:45:21.123Z",
    "data": {
      "error": "Username or email already exists."
    },
    "statusCode": 400
  }
  ```
- **Example**:
  ```
  curl -X POST http://localhost:9191/api/v1/auth/register -H "Content-Type: application/json" -d '{"username":"user123","email":"user123@example.com","password":"password123","firstName":"John","lastName":"Doe"}'
  ```

---
