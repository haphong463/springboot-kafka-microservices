
# Identity Service API Documentation 🧑‍💻

## Base URL: `http://localhost:9191`

## Authentication Endpoints 🛡️

### 1. **User Login**
- **Example**:
  ```bash
  curl -X POST http://localhost:9191/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"username":"user123","password":"password123"}'
  ```

---

### 2. **User Registration**
- **Example**:
  ```bash
  curl -X POST http://localhost:9191/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"user123","email":"user123@example.com","password":"password123"}'
  ```

---

# Order Service 📦

## Base URL: `http://localhost:9191/api/v1/order`

### 1. **Place Order**

```bash
curl -X POST http://localhost:9191/api/v1/order \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <JWT_TOKEN>" \
-d '{
    "orderItems": [
        {
            "productId": "0c0e9912-969e-4141-b3c0-7efb2b3be8e7",
            "quantity": 1
        },
        {
            "productId": "768aee20-dd48-4458-88bd-07bfff726e27",
            "quantity": 1
        }
    ]
}'
```

### 2. **Get Order Details**

```bash
curl -X GET http://localhost:9191/api/v1/order/{orderId} \
-H "Authorization: Bearer <JWT_TOKEN>"
```

---

# Product Service 🛒

## Base URL: `http://localhost:9191/api/v1/products`

### 1. **Create Product**

```bash
curl -X POST http://localhost:9191/api/v1/products \
-H "Content-Type: application/json" \
-H "Authorization: Bearer <JWT_TOKEN>" \
-d '{
    "name": "IPhone 28",
    "description": "IPhone 28 - description",
    "imageUrl": "image1.jpg",
    "price": 51.5,
    "stockQuantity": 10
}'
```

### 2. **Get Product Details**

```bash
curl -X GET http://localhost:9191/api/v1/products/{productId} \
-H "Authorization: Bearer <JWT_TOKEN>"
```

> **Note**: Replace `<JWT_TOKEN>` with the actual token you receive after logging in to the system.

---
