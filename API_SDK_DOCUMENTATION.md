# 🚗 RushHour Backend API SDK Documentation

## 📋 Table of Contents

- [Overview](#-overview)
- [Authentication](#-authentication)
- [Base URL and Headers](#-base-url-and-headers)
- [Data Types](#-data-types)
- [API Endpoints](#-api-endpoints)
  - [Health Check](#health-check)
  - [Authentication](#authentication-endpoints)
  - [Users](#user-endpoints)
  - [Cars](#car-endpoints)
  - [User-Car Relationships](#user-car-relationships)
  - [Car Relations (Blocking)](#car-relations-blocking)
  - [Notifications](#notification-endpoints)
- [Error Handling](#-error-handling)
- [Examples](#-examples)

---

## 🌟 Overview

The RushHour Backend API provides a comprehensive Restful interface for managing users, cars, and their relationships. The API supports OAuth2 authentication (Google, Apple, Facebook) and uses JWT tokens for session management.

**Base URL**: `https://your-domain.com` (Production) / `http://localhost:8008` (Development)

---

## 🔐 Authentication

### OAuth2 Providers Supported
- **Google** Sign in with Google
- **Apple** Sign in with Apple  
- **Facebook** Login with Facebook

### User Consent Flow
The API implements a consent-based authentication flow for new users:

1. **New User First Login**: User attempts OAuth login without consent parameter
2. **Consent Required Error**: API returns `403 Forbidden` with `USER_CONSENT_REQUIRED` error
3. **Client Shows Agreement**: Frontend displays consent agreement to user
4. **User Agrees**: User reads and accepts the terms
5. **Second Login**: Client calls OAuth endpoint again with `agreedConsent=true`
6. **Success**: User is created and authenticated successfully

**Existing users** do not require consent and can log in directly.

### JWT Token Management
- **Token Expiration**: 24 hours
- **Refresh**: Available via `/api/v1/auth/refresh`
- **Header**: `Authorization: Bearer <jwt_token>`

---

## 📡 Base URL and Headers

### Base Headers
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Authorization': 'Bearer <jwt_token>'
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
  IL = 1
}
```

#### Brands
```typescript
enum Brands {
  // Integer values representing car brands
  // Examples: AIWAYS = 0, TOYOTA = 79, BMW = 7, etc.
  // Full list available in API responses
  UNKNOWN = 999
}
```

#### Colors
```typescript
enum Colors {
  // Integer values representing car colors
  // Examples: UNKNOWN = 0, WHITE = 100, BLACK = 10, RED = 50, etc.
  // Full list available in API responses
  UNKNOWN = 0,
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

### Request DTOs

#### OAuthLoginRequestDTO
```typescript
interface OAuthLoginRequestDTO {
  token: string
}
```

#### UserCreationDTO
```typescript
interface UserCreationDTO {
  firstName: string;
  lastName: string;
  email: string;
  pushNotificationToken: string;
  urlPhoto?: string;
}
```

#### FindCarRequestDTO
```typescript
interface FindCarRequestDTO {
  plateNumber: string;
  country: Countries;
  userId?: number;
}
```

#### UserCarRequestDTO
```typescript
interface UserCarRequestDTO {
  userId: number;
  carId: number;
}
```

#### CarsRelationRequestDTO
```typescript
interface CarsRelationRequestDTO {
  blockingCarId: number;
  blockedCarId: number;
  userCarSituation: UserCarSituation;
}
```

### Response DTOs

#### AuthResponseDTO
```typescript
interface AuthResponseDTO {
  token: string;
  user: UserDTO;
}
```

#### UserDTO
```typescript
interface UserDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  urlPhoto?: string;
  pushNotificationToken?: string;
}
```

#### CarDTO
```typescript
interface CarDTO {
  id: number;
  plateNumber: string;
  country: Countries;
  brand: Brands;
  model: string;
  color: Colors;
  carLicenseExpireDate?: string;
  hasOwner: boolean;
}
```

#### UserCarsDTO
```typescript
interface UserCarsDTO {
  user: UserDTO;
  cars: CarDTO[];
}
```

#### CarUsersDTO
```typescript
interface CarUsersDTO {
  car: CarDTO;
  users: UserDTO[];
}
```

#### CarRelationsDTO
```typescript
interface CarRelationsDTO {
  car: CarDTO;
  isBlocking: CarDTO[];
  isBlockedBy: CarDTO[];
  message?: string;
}
```

**Message Field Values**:
- `null` - No notification action taken (e.g., GET requests)
- `"Blocking relationship created. Notifications sent to owners."` - Successfully sent notifications
- `"Blocking relationship created. No notifications sent - car has no registered owners."` - No notifications sent due to a missing car owner
- `"Blocking relationship removed. Notifications sent to owners."` - Successfully sent "free to go" notifications
- `"Blocking relationship removed. No notifications sent - car has no registered owners."` - No "free to go" notifications sent due to a missing car owner

---

## 🔌 API Endpoints

### Health Check

#### 1. Health Check
```http
GET /api/v1/health
```

**Response**:
```json
{
  "status": "UP",
  "timestamp": "2024-07-27T20:01:16.620",
  "service": "RushHour Backend",
  "version": "1.0.0"
}
```

---

### Authentication Endpoints

#### 1. Google OAuth2 Login
```http
POST /api/v1/auth/google?agreedConsent=true
Content-Type: application/json

{
  "token": "google_id_token_from_client"
}
```

**Parameters**:
- `agreedConsent` (optional): Boolean indicating user consent for new users

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

**Error Response (New User Without Consent)**:
```json
{
  "cause": "User consent is required for OAuth login",
  "errorCode": 403
}
```

#### 2. Facebook Login
```http
POST /api/v1/auth/facebook?agreedConsent=true
Content-Type: application/json

{
  "token": "facebook_access_token_from_client"
}
```

**Parameters**:
- `agreedConsent` (optional): Boolean indicating user consent for new users

**Response**: Same as Google login

**Error Response (New User Without Consent)**:
```json
{
  "cause": "User consent is required for OAuth login",
  "errorCode": 403
}
```

#### 3. Apple Sign In
```http
POST /api/v1/auth/apple?agreedConsent=true
Content-Type: application/json

{
  "token": "apple_id_token_from_client"
}
```

**Parameters**:
- `agreedConsent` (optional): Boolean indicating user consent for new users

**Response**: Same as Google login

**Error Response (New User Without Consent)**:
```json
{
  "cause": "User consent is required for OAuth login",
  "errorCode": 403
}
```

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

#### 1. Get Current User
```http
GET /api/v1/user
Authorization: Bearer <jwt_token>
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

#### 2. Deactivate Current User
```http
PUT /api/v1/user/deactivate
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
  "carLicenseExpireDate": "2025-12-31T23:59:59",
  "hasOwner": true
}
```

**Notes**:
- `hasOwner` field indicates whether the car has any registered owners in the system
- This field is calculated in real-time based on user-car relationships
- New cars without owners will have `hasOwner: false`

---

### User-Car Relationships

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

#### 2. Get Current User's Cars
```http
GET /api/v1/user-car
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

### Car Relations (Blocking)

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
  "isBlockedBy": [],
  "message": "Blocking relationship created. Notifications sent to owners."
}
```

**Response when car has no owner**:
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
  "isBlockedBy": [],
  "message": "Blocking relationship created. No notifications sent - car has no registered owners."
}
```

#### 2. Get Car's Blocking Relationships
```http
GET /api/v1/car-relations/by-car-id?carId=456
Authorization: Bearer <jwt_token>
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
  "isBlockedBy": [],
  "message": null
}
```

#### 3. Get Current User's Car Relations
```http
GET /api/v1/car-relations/by-user
Authorization: Bearer <jwt_token>
```

**Response**: List of CarRelationsDTO for all cars owned by the current user

#### 4. Remove Car Blocking Relationship
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
  "isBlocking": [],
  "isBlockedBy": [],
  "message": "Blocking relationship removed. Notifications sent to owners."
}
```

**Response when car has no owner**:
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
  "isBlocking": [],
  "isBlockedBy": [],
  "message": "Blocking relationship removed. No notifications sent - car has no registered owners."
}
```

#### 5. Remove All Car Relations
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

**Error Response (Car is not blocked)**:
```json
{
  "cause": "Car is not blocked by any other car",
  "errorCode": 400
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

| Status | Description           | Common Causes                  |
|--------|-----------------------|--------------------------------|
| `200`  | Success               | Request completed successfully |
| `201`  | Created               | Resource created successfully  |
| `400`  | Bad Request           | Invalid request data           |
| `401`  | Unauthorized          | Missing or invalid JWT token   |
| `403`  | Forbidden             | Insufficient permissions       |
| `404`  | Not Found             | Resource not found             |
| `409`  | Conflict              | Resource already exists        |
| `422`  | Unprocessable Entity  | Validation errors              |
| `500`  | Internal Server Error | Server error                   |

### Common Error Scenarios

#### Authentication Errors
```json
{
  "cause": "Invalid JWT token",
  "errorCode": 401
}
```

#### Consent Required Error (New Users)
```json
{
  "cause": "User consent is required for OAuth login",
  "errorCode": 403
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

#### Car Without Owner (Mixed Handling)

**Car Relations Endpoints** (Handled Gracefully):
- Creating/removing the blocking relationship successfully
- Including a message in the response indicating no notifications were sent
- Returning HTTP 200 instead of HTTP 403

**Example Response**:
```json
{
  "car": {
    "id": 456,
    "plateNumber": "ABC123",
    "country": "IL",
    "brand": "TOYOTA",
    "model": "Corolla",
    "color": "WHITE"
  },
  "isBlocking": [
    {
      "id": 789,
      "plateNumber": "XYZ789",
      "country": "IL",
      "brand": "HONDA",
      "model": "Civic",
      "color": "BLACK"
    }
  ],
  "isBlockedBy": [],
  "message": "Blocking relationship created. No notifications sent - car has no registered owners."
}
```

**Direct Notification Endpoints** (Still Throw 403):
- The `/api/v1/notification/send-need-to-go` endpoint still throws HTTP 403 when the car has no owner
- This is because it's a direct notification request that cannot be fulfilled

**Example Error Response**:
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

  async googleLogin(idToken: string, agreedConsent?: boolean) {
    const url = new URL(`${this.baseURL}/api/v1/auth/google`);
    if (agreedConsent !== undefined) {
      url.searchParams.set('agreedConsent', agreedConsent.toString());
    }

    const response = await fetch(url.toString(), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token: idToken })
    });
    
    if (!response.ok) {
      const error = await response.json();
      if (error.errorCode === 403 && error.cause.includes('consent')) {
        throw new Error('USER_CONSENT_REQUIRED');
      }
      throw new Error(error.cause || 'Authentication failed');
    }
    
    const data = await response.json();
    this.token = data.token;
    return data;
  }

  async loginWithConsent(idToken: string) {
    try {
      return await this.googleLogin(idToken);
    } catch (error) {
      if (error.message === 'USER_CONSENT_REQUIRED') {
        const userAgreed = await this.showConsentAgreement();
        if (userAgreed) {
          return await this.googleLogin(idToken, true);
        } else {
          throw new Error('User declined consent');
        }
      }
      throw error;
    }
  }

  private async showConsentAgreement(): Promise<boolean> {
    return new Promise((resolve) => {
      const agreed = confirm('Do you agree to our terms and conditions?');
      resolve(agreed);
    });
  }

  async getUserCars() {
    const response = await fetch(
      `${this.baseURL}/api/v1/user-car`,
      {
        headers: {
          'Authorization': `Bearer ${this.token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    return response.json();
  }

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
    
    const result = await response.json();
    
    if (result.message) {
      console.log('Notification status:', result.message);
    }
    
    return result;
  }
}

const api = new RushHourAPI();

try {
  const authData = await api.loginWithConsent('google_id_token');
  console.log('Logged in user:', authData.user);
} catch (error) {
  if (error.message === 'User declined consent') {
    console.log('User declined to agree to terms');
  } else {
    console.error('Login failed:', error.message);
  }
}

const userCars = await api.getUserCars();
console.log('User cars:', userCars.cars);

const carRelations = await api.createCarBlocking(456, 789);
console.log('Car relations:', carRelations);
console.log('Notification status:', carRelations.message);
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
  hasOwner: boolean;
}

const useRushHourAPI = () => {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [showConsent, setShowConsent] = useState(false);

  const login = async (provider: 'google' | 'facebook' | 'apple', token: string, agreedConsent?: boolean) => {
    const url = new URL(`/api/v1/auth/${provider}`);
    if (agreedConsent !== undefined) {
      url.searchParams.set('agreedConsent', agreedConsent.toString());
    }

    const response = await fetch(url.toString(), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ token })
    });
    
    if (!response.ok) {
      const error = await response.json();
      if (error.errorCode === 403 && error.cause.includes('consent')) {
        setShowConsent(true);
        throw new Error('USER_CONSENT_REQUIRED');
      }
      throw new Error(error.cause || 'Authentication failed');
    }
    
    const data = await response.json();
    setToken(data.token);
    setUser(data.user);
    return data;
  };

  const loginWithConsent = async (provider: 'google' | 'facebook' | 'apple', token: string) => {
    try {
      return await login(provider, token);
    } catch (error) {
      if (error.message === 'USER_CONSENT_REQUIRED') {
        setShowConsent(true);
        throw error;
      }
      throw error;
    }
  };

  const handleConsentAgreement = async (agreed: boolean) => {
    setShowConsent(false);
    if (agreed) {
      return await login('google', 'stored_token', true);
    }
    throw new Error('User declined consent');
  };

  const getUserCars = async () => {
    if (!token) throw new Error('Not authenticated');
    
    const response = await fetch(`/api/v1/user-car`, {
      headers: { 'Authorization': `Bearer ${token}` }
    });
    
    return response.json();
  };

  return { 
    login, 
    loginWithConsent, 
    handleConsentAgreement,
    getUserCars, 
    user, 
    token, 
    showConsent 
  };
};
```

### cURL Examples

#### Health Check
```bash
curl -X GET http://localhost:8008/api/v1/health
```

#### Google Login
```bash
curl -X POST http://localhost:8008/api/v1/auth/google \
  -H "Content-Type: application/json" \
  -d '{"token": "google_id_token_here"}'

curl -X POST "http://localhost:8008/api/v1/auth/google?agreedConsent=true" \
  -H "Content-Type: application/json" \
  -d '{"token": "google_id_token_here"}'
```

#### Get User's Cars
```bash
curl -X GET "http://localhost:8008/api/v1/user-car" \
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

**Example Response**:
```json
{
  "car": {
    "id": 456,
    "plateNumber": "ABC123",
    "country": "IL",
    "brand": "TOYOTA",
    "model": "Corolla",
    "color": "WHITE"
  },
  "isBlocking": [
    {
      "id": 789,
      "plateNumber": "XYZ789",
      "country": "IL",
      "brand": "HONDA",
      "model": "Civic",
      "color": "BLACK"
    }
  ],
  "isBlockedBy": [],
  "message": "Blocking relationship created. Notifications sent to owners."
}
```

---

## 🔧 Development Setup

### Local Development
```bash
./gradlew bootRun

./rushhour.sh

const API_BASE_URL = 'http://localhost:8008';
```

### Production
```bash
const API_BASE_URL = 'https://your-production-domain.com';
```

---

## 📚 Additional Resources

- [Security Documentation ](./SECURITY.md)- Authentication and security details
- [Error Codes](./src/main/kotlin/com/yb/rh/error/ErrorType.kt) - Complete error type definitions
- [Data Models](./src/main/kotlin/com/yb/rh/dtos) - Complete DTO definitions

---

## 🆘 Support

For API support and questions:
- Check the [Error Handling](#-error-handling) section
- Review the [Examples](#-examples) section
- Contact the backend team for technical issues

---

**Last Updated**: January 2025  
**API Version**: v1  
**Authentication**: OAuth2 + JWT  
**Framework**: Spring Boot 3.2.0 + Kotlin  
**Database**: MySQL (Production) / H2 (Testing) 