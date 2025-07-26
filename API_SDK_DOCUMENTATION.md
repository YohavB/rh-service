# 🚗 RushHour Backend API SDK Documentation

## 📋 Table of Contents

- [Overview](#overview)
- [Authentication](#authentication)
- [Base URL & Headers](#base-url--headers)
- [Data Types](#data-types)
- [API Endpoints](#api-endpoints)
  - [Authentication](#authentication-endpoints)
  - [Users](#user-endpoints)
  - [Cars](#car-endpoints)
  - [User-Car Relationships](#user-car-endpoints)
  - [Car Relations (Blocking)](#car-relations-endpoints)
  - [Notifications](#notification-endpoints)
- [Error Handling](#error-handling)
- [Examples](#examples)

---

## 🌟 Overview

The RushHour Backend API provides a comprehensive RESTful interface for managing users, cars, and their relationships. The API supports OAuth2 authentication (Google, Apple, Facebook) and uses JWT tokens for session management.

**Base URL**: `https://your-domain.com` (Production) / `http://localhost:8008` (Development)

---

## 🔐 Authentication

### OAuth2 Providers Supported
- **Google** - Sign in with Google
- **Apple** - Sign in with Apple  
- **Facebook** - Login with Facebook

### JWT Token Management
- **Token Expiration**: 24 hours
- **Refresh**: Available via `/api/v1/auth/refresh`
- **Header**: `Authorization: Bearer <jwt_token>`

---

## 📡 Base URL & Headers

### Base Headers
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer <jwt_token>' // Required for protected endpoints
};
```

### CORS Support
- **Allowed Origins**: All origins (configurable)
- **Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Headers**: All headers allowed

---

## 📊 Data Types

### Enums

#### Countries
```typescript
enum Countries {
  UNKNOWN = 0,
  IL = 1  // Israel
}
```

#### Brands
```typescript
enum Brands {
  AIWAYS = 0,
  ALPHA_ROMEO = 2,
  ASTON_MARTIN = 3,
  AUDI = 4,
  // ... (see full list in Brands.kt)
  UNKNOWN = 999
}
```

#### Colors
```typescript
enum Colors {
  UNKNOWN = 0,
  PEARL_GREEN = 1,
  PEARL_BLACK = 2,
  // ... (see full list in Colors.kt)
  WHITE = 100
}
```

#### UserCarSituation
```typescript
enum UserCarSituation {
  IS_BLOCKING = "IS_BLOCKING",
  IS_BLOCKED = "IS_BLOCKED"
}
```

---

## 🔌 API Endpoints

### Authentication Endpoints

#### 1. Google OAuth2 Login
```http
POST /api/v1/auth/google
Content-Type: application/json

{
  "token": "google_id_token_from_client"
}
```

**Response**:
```json
{
  "token": "jwt_token_for_api_access",
  "user": {
    "id": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "urlPhoto": "https://example.com/photo.jpg"
  }
}
```

#### 2. Facebook Login
```http
POST /api/v1/auth/facebook
Content-Type: application/json

{
  "token": "facebook_access_token_from_client"
}
```

**Response**: Same as Google login

#### 3. Apple Sign In
```http
POST /api/v1/auth/apple
Content-Type: application/json

{
  "token": "apple_id_token_from_client"
}
```

**Response**: Same as Google login

#### 4. Refresh JWT Token
```http
POST /api/v1/auth/refresh
Authorization: Bearer <current_jwt_token>
```

**Response**:
```json
{
  "token": "new_jwt_token",
  "user": {
    "id": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "urlPhoto": "https://example.com/photo.jpg"
  }
}
```

#### 5. Logout (Client-side)
```http
POST /api/v1/auth/logout
Authorization: Bearer <jwt_token>
```

**Response**: `200 OK` (Client should discard token)

---

### User Endpoints

#### 1. Create User
```http
POST /api/v1/user
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "pushNotificationToken": "expo_push_token",
  "urlPhoto": "https://example.com/photo.jpg"
}
```

**Response**:
```json
{
  "id": 123,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "urlPhoto": "https://example.com/photo.jpg"
}
```

#### 2. Get User by ID
```http
GET /api/v1/user?id=123
Authorization: Bearer <jwt_token>
```

**Response**: Same as Create User response

#### 3. Get User by Email
```http
GET /api/v1/user/by-email?email=john.doe@example.com
Authorization: Bearer <jwt_token>
```

**Response**: Same as Create User response

#### 4. Update User
```http
PUT /api/v1/user
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "id": 123,
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com",
  "urlPhoto": "https://example.com/new-photo.jpg"
}
```

**Response**: Same as Create User response

#### 5. Deactivate User
```http
PUT /api/v1/user/deactivate/123
Authorization: Bearer <jwt_token>
```

**Response**: `200 OK`

#### 6. Activate User
```http
PUT /api/v1/user/activate/123
Authorization: Bearer <jwt_token>
```

**Response**: `200 OK`

---

### Car Endpoints

#### 1. Find/Create Car
```http
POST /api/v1/car
Content-Type: application/json

{
  "plateNumber": "ABC123",
  "country": "IL",
  "userId": 123
}
```

**Response**:
```json
{
  "id": 456,
  "plateNumber": "ABC123",
  "country": "IL",
  "brand": "TOYOTA",
  "model": "Corolla",
  "color": "WHITE",
  "carLicenseExpireDate": "2025-12-31T23:59:59"
}
```

---

### User-Car Endpoints

#### 1. Assign Car to User
```http
POST /api/v1/user-car
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "userId": 123,
  "carId": 456
}
```

**Response**:
```json
{
  "user": {
    "id": 123,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "urlPhoto": "https://example.com/photo.jpg"
  },
  "cars": [
    {
      "id": 456,
      "plateNumber": "ABC123",
      "country": "IL",
      "brand": "TOYOTA",
      "model": "Corolla",
      "color": "WHITE",
      "carLicenseExpireDate": "2025-12-31T23:59:59"
    }
  ]
}
```

#### 2. Get User's Cars
```http
GET /api/v1/user-car/by-user-id?userId=123
Authorization: Bearer <jwt_token>
```

**Response**: Same as Assign Car response

#### 3. Remove Car from User
```http
DELETE /api/v1/user-car
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "userId": 123,
  "carId": 456
}
```

**Response**: Same as Assign Car response (updated list)

---

### Car Relations Endpoints

#### 1. Create Car Blocking Relationship
```http
POST /api/v1/car-relations
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "blockingCarId": 456,
  "blockedCarId": 789,
  "userCarSituation": "IS_BLOCKING"
}
```

**Response**:
```json
{
  "car": {
    "id": 456,
    "plateNumber": "ABC123",
    "country": "IL",
    "brand": "TOYOTA",
    "model": "Corolla",
    "color": "WHITE",
    "carLicenseExpireDate": "2025-12-31T23:59:59"
  },
  "isBlocking": [
    {
      "id": 789,
      "plateNumber": "XYZ789",
      "country": "IL",
      "brand": "HONDA",
      "model": "Civic",
      "color": "BLACK",
      "carLicenseExpireDate": "2025-12-31T23:59:59"
    }
  ],
  "isBlockedBy": []
}
```

**Error Response (Car has no owner)**:
```json
{
  "cause": "This car has no user so no one would be notified. Consider finding the owner and telling them to use this app.",
  "errorCode": 403
}
```

#### 2. Get Car's Blocking Relationships
```http
GET /api/v1/car-relations?carId=456
Authorization: Bearer <jwt_token>
```

**Response**: Same as Create Car Relations response

#### 3. Remove Car Blocking Relationship
```http
DELETE /api/v1/car-relations
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "blockingCarId": 456,
  "blockedCarId": 789,
  "userCarSituation": "IS_BLOCKING"
}
```

**Response**: Same as Create Car Relations response (updated list)

#### 4. Remove All Car Relations
```http
DELETE /api/v1/car-relations/all-by-car-id?carId=456
Authorization: Bearer <jwt_token>
```

**Response**: `200 OK`

---

### Notification Endpoints

#### 1. Send "Need to Go" Notification
```http
POST /api/v1/notification/send-need-to-go?blockedCarId=789
Authorization: Bearer <jwt_token>
```

**Response**:
```json
{
  "entity": "Notification sent successfully"
}
```

**Error Response (Car has no owner)**:
```json
{
  "cause": "This car has no user so no one would be notified. Consider finding the owner and telling them to use this app.",
  "errorCode": 403
}
```

---

## ❌ Error Handling

### Error Response Format
```json
{
  "cause": "Error message description",
  "errorCode": 400
}
```

### HTTP Status Codes

| Status | Description | Common Causes |
|--------|-------------|---------------|
| `200` | Success | Request completed successfully |
| `201` | Created | Resource created successfully |
| `400` | Bad Request | Invalid request data |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Insufficient permissions |
| `404` | Not Found | Resource not found |
| `409` | Conflict | Resource already exists |
| `422` | Unprocessable Entity | Validation errors |
| `500` | Internal Server Error | Server error |

### Common Error Scenarios

#### Authentication Errors
```json
{
  "cause": "Invalid JWT token",
  "errorCode": 401
}
```

#### Validation Errors
```json
{
  "cause": "Value invalid@email for `email` must be a well-formed email address"
}
```

#### Resource Not Found
```json
{
  "cause": "User not found with id: 999"
}
```

#### Car Has No Owner (Notification Errors)
```json
{
  "cause": "This car has no user so no one would be notified. Consider finding the owner and telling them to use this app.",
  "errorCode": 403
}
```

---

## 💡 Examples

### JavaScript/TypeScript SDK Example

```typescript
class RushHourAPI {
  private baseURL: string;
  private token: string | null = null;

  constructor(baseURL: string = 'http://localhost:8008') {
    this.baseURL = baseURL;
  }

  // Authentication
  async googleLogin(idToken: string) {
    const response = await fetch(`${this.baseURL}/api/v1/auth/google`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: idToken })
    });
    
    const data = await response.json();
    this.token = data.token;
    return data;
  }

  // Get user's cars
  async getUserCars(userId: number) {
    const response = await fetch(
      `${this.baseURL}/api/v1/user-car/by-user-id?userId=${userId}`,
      {
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    return response.json();
  }

  // Create car blocking relationship
  async createCarBlocking(blockingCarId: number, blockedCarId: number) {
    const response = await fetch(`${this.baseURL}/api/v1/car-relations`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${this.token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        blockingCarId,
        blockedCarId,
        userCarSituation: 'IS_BLOCKING'
      })
    });
    
    return response.json();
  }
}

// Usage
const api = new RushHourAPI();

// Login
const authData = await api.googleLogin('google_id_token');
console.log('Logged in user:', authData.user);

// Get user's cars
const userCars = await api.getUserCars(authData.user.id);
console.log('User cars:', userCars.cars);
```

### React Hook Example

```typescript
import { useState, useEffect } from 'react';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  urlPhoto?: string;
}

interface Car {
  id: number;
  plateNumber: string;
  country: string;
  brand: string;
  model: string;
  color: string;
  carLicenseExpireDate?: string;
}

const useRushHourAPI = () => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);

  const login = async (provider: 'google' | 'facebook' | 'apple', token: string) => {
    const response = await fetch(`/api/v1/auth/${provider}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token })
    });
    
    const data = await response.json();
    setToken(data.token);
    setUser(data.user);
    return data;
  };

  const getUserCars = async (userId: number) => {
    if (!token) throw new Error('Not authenticated');
    
    const response = await fetch(`/api/v1/user-car/by-user-id?userId=${userId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    return response.json();
  };

  return { login, getUserCars, user, token };
};
```

### cURL Examples

#### Google Login
```bash
curl -X POST http://localhost:8008/api/v1/auth/google \
  -H "Content-Type: application/json" \
  -d '{"token": "google_id_token_here"}'
```

#### Get User's Cars
```bash
curl -X GET "http://localhost:8008/api/v1/user-car/by-user-id?userId=123" \
  -H "Authorization: Bearer your_jwt_token_here"
```

#### Create Car Blocking
```bash
curl -X POST http://localhost:8008/api/v1/car-relations \
  -H "Authorization: Bearer your_jwt_token_here" \
  -H "Content-Type: application/json" \
  -d '{
    "blockingCarId": 456,
    "blockedCarId": 789,
    "userCarSituation": "IS_BLOCKING"
  }'
```

---

## 🔧 Development Setup

### Local Development
```bash
# Start the backend server
./run.sh dev

# Base URL for local development
const API_BASE_URL = 'http://localhost:8008';
```

### Production
```bash
# Base URL for production
const API_BASE_URL = 'https://your-production-domain.com';
```

---

## 📚 Additional Resources

- [Security Documentation](./SECURITY.md) - Authentication and security details
- [Error Codes](./src/main/kotlin/com/yb/rh/error/ErrorType.kt) - Complete error type definitions
- [Data Models](./src/main/kotlin/com/yb/rh/dtos/) - Complete DTO definitions

---

## 🆘 Support

For API support and questions:
- Check the [Error Handling](#error-handling) section
- Review the [Examples](#examples) section
- Contact the backend team for technical issues

---

**Last Updated**: July 2024  
**API Version**: v1  
**Authentication**: OAuth2 + JWT 