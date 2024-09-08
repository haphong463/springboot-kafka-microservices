# Identity Service API Documentation 🧑‍💻

## Base URL: `http://localhost:9191`

## Authentication Endpoints 🛡️
- **Example**:
  ```
  curl -X POST http://localhost:9191/api/v1/auth/login -H "Content-Type: application/json" -d '{"username":"user123","password":"password123"}'
  ```

---

### 2. **User Registration**
- **Example**:
  ```
  curl -X POST http://localhost:9191/api/v1/auth/register -H "Content-Type: application/json" -d '{"username":"user123","email":"user123@example.com","password":"password123","firstName":"John","lastName":"Doe"}'
  ```

---
