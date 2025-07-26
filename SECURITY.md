# 🔐 Security Implementation Guide

## Overview

This document describes the security implementation for the RushHour backend, which uses **Google OAuth2, Apple Sign In, and Facebook Login** for authentication and **JWT tokens** for session management.

## 🔑 Authentication Flow

### 1. Multi-OAuth Login Flow

```mermaid
sequenceDiagram
    participant Client
    participant Backend
    participant OAuth Provider
    participant Database

    Client->>OAuth Provider: Request Sign-In (Google/Apple/Facebook)
    OAuth Provider->>Client: Return Token
    Client->>Backend: POST /api/v1/auth/{provider} with Token
    Backend->>OAuth Provider: Verify Token
    OAuth Provider->>Backend: Token verification result
    Backend->>Database: Find/Create user by email
    Database->>Backend: User data
    Backend->>Backend: Generate JWT token
    Backend->>Client: Return JWT token + user data
```

### 2. API Request Flow

```mermaid
sequenceDiagram
    participant Client
    participant Backend
    participant Database

    Client->>Backend: API Request with JWT token
    Backend->>Backend: Validate JWT token
    Backend->>Database: Load user details
    Database->>Backend: User data
    Backend->>Backend: Process request
    Backend->>Client: API Response
```

## 🛠️ Implementation Details

### Security Components

1. **JwtTokenProvider** - Handles JWT token generation and validation
2. **GoogleTokenVerifier** - Verifies Google ID tokens
3. **FacebookTokenVerifier** - Verifies Facebook access tokens
4. **AppleTokenVerifier** - Verifies Apple ID tokens
5. **JwtAuthenticationFilter** - Intercepts requests and validates JWT tokens
6. **CustomUserDetailsService** - Loads user details for Spring Security
7. **SecurityConfig** - Configures Spring Security
8. **AuthController** - Handles authentication endpoints for all providers

### Key Features

- ✅ **Multi-OAuth Support** - Google, Apple, and Facebook authentication
- ✅ **Unified User Management** - Single user database for all providers
- ✅ **Email-based Identity** - Users identified by email across providers
- ✅ **JWT Token Management** - Stateless session management
- ✅ **Automatic User Creation** - Users are created on first login
- ✅ **Token Refresh** - Automatic token refresh capability
- ✅ **CORS Support** - Cross-origin request handling
- ✅ **Role-based Access** - User roles and permissions
- ✅ **HTTPS Support** - Production-ready SSL configuration

## 📋 API Endpoints

### Authentication Endpoints

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/v1/auth/google` | POST | Google OAuth2 login | No |
| `/api/v1/auth/facebook` | POST | Facebook Login | No |
| `/api/v1/auth/apple` | POST | Apple Sign In | No |
| `/api/v1/auth/refresh` | POST | Refresh JWT token | Yes |
| `/api/v1/auth/logout` | POST | Logout (client-side) | Yes |

### Protected Endpoints

All other API endpoints require authentication via JWT token in the `Authorization` header:

```
Authorization: Bearer <jwt_token>
```

## 🔧 Configuration

### Environment Variables

```bash
# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRATION=86400000  # 24 hours in milliseconds

# Google OAuth2 Configuration
GOOGLE_CLIENT_ID=your-google-oauth2-client-id

# Facebook OAuth2 Configuration
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_APP_SECRET=your-facebook-app-secret

# Apple Sign In Configuration
APPLE_CLIENT_ID=your-apple-client-id
APPLE_TEAM_ID=your-apple-team-id
APPLE_KEY_ID=your-apple-key-id

# SSL Configuration (Production)
SSL_KEYSTORE_PASSWORD=your-keystore-password
```

### Application Properties

```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:default-secret-for-development}
jwt.expiration=86400000

# Google OAuth2 Configuration
google.client.id=${GOOGLE_CLIENT_ID:your-google-client-id}

# Facebook OAuth2 Configuration
facebook.app.id=${FACEBOOK_APP_ID:your-facebook-app-id}
facebook.app.secret=${FACEBOOK_APP_SECRET:your-facebook-app-secret}

# Apple Sign In Configuration
apple.client.id=${APPLE_CLIENT_ID:your-apple-client-id}
apple.team.id=${APPLE_TEAM_ID:your-apple-team-id}
apple.key.id=${APPLE_KEY_ID:your-apple-key-id}

# HTTPS Configuration (Production)
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
```

## 🚀 Setup Instructions

### 1. Google OAuth2 Setup

1. **Create Google Cloud Project**
   ```bash
   # Go to https://console.cloud.google.com
   # Create a new project or select existing one
   ```

2. **Enable Google+ API**
   ```bash
   # In Google Cloud Console
   # APIs & Services > Library
   # Search for "Google+ API" and enable it
   ```

3. **Create OAuth2 Credentials**
   ```bash
   # APIs & Services > Credentials
   # Create Credentials > OAuth 2.0 Client IDs
   # Application type: Web application
   # Authorized redirect URIs: Add your app URLs
   ```

4. **Get Client ID**
   ```bash
   # Copy the Client ID from the created credentials
   # Add to your environment variables
   ```

### 2. Facebook Login Setup

1. **Create Facebook App**
   ```bash
   # Go to https://developers.facebook.com
   # Create a new app or select existing one
   ```

2. **Add Facebook Login Product**
   ```bash
   # In your Facebook app
   # Add Product > Facebook Login
   # Configure settings for your platform
   ```

3. **Get App ID and Secret**
   ```bash
   # Copy the App ID and App Secret
   # Add to your environment variables
   ```

### 3. Apple Sign In Setup

1. **Create Apple Developer Account**
   ```bash
   # Go to https://developer.apple.com
   # Create account or use existing one
   ```

2. **Configure Sign in with Apple**
   ```bash
   # Certificates, Identifiers & Profiles
   # Identifiers > App IDs
   # Enable "Sign in with Apple" capability
   ```

3. **Create Service ID**
   ```bash
   # Identifiers > Services IDs
   # Create new Service ID
   # Configure Sign in with Apple
   ```

4. **Get Credentials**
   ```bash
   # Copy Client ID, Team ID, and Key ID
   # Add to your environment variables
   ```

### 4. Environment Configuration

```bash
# Copy environment template
cp env.example .env

# Edit .env file
nano .env

# Set your values:
JWT_SECRET=your-super-secret-jwt-key
GOOGLE_CLIENT_ID=your-google-client-id
FACEBOOK_APP_ID=your-facebook-app-id
FACEBOOK_APP_SECRET=your-facebook-app-secret
APPLE_CLIENT_ID=your-apple-client-id
APPLE_TEAM_ID=your-apple-team-id
APPLE_KEY_ID=your-apple-key-id
```

### 5. Database Migration

```bash
# No additional migrations needed - uses existing user table
# Users are identified by email across all providers
```

### 6. SSL Certificate (Production)

```bash
# Generate SSL certificate
keytool -genkeypair -alias rushhour -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650

# Move to resources directory
mv keystore.p12 src/main/resources/
```

## 🔒 Security Best Practices

### 1. Unified User Management

Our implementation uses a **unified user management approach**:

- **Email as Primary Identifier**: Users are uniquely identified by email across all OAuth providers
- **No Additional Database Fields**: Uses existing user table without OAuth-specific fields
- **Seamless Provider Switching**: Users can switch between providers using the same email
- **Automatic User Creation**: New users are created automatically on first login
- **Provider Agnostic**: All providers generate the same JWT tokens for API access

**Benefits:**
- ✅ **Simpler Schema** - No unnecessary database fields
- ✅ **Better Performance** - Fewer database columns
- ✅ **Easier Maintenance** - Less complexity
- ✅ **Future-Proof** - Easy to add more providers later

### 2. JWT Security

- ✅ **Strong Secret Key** - Use cryptographically strong random key
- ✅ **Token Expiration** - Set reasonable expiration time (24 hours)
- ✅ **HTTPS Only** - Always use HTTPS in production
- ✅ **Token Validation** - Validate tokens on every request

### 2. Multi-OAuth Security

- ✅ **Provider Validation** - Verify each provider's credentials
- ✅ **Token Verification** - Always verify tokens with respective providers
- ✅ **Email Verification** - Ensure email is verified across all providers
- ✅ **Token Expiration** - Check token expiration for all providers
- ✅ **Unified User Identity** - Users identified by email across providers

### 3. General Security

- ✅ **Input Validation** - Validate all inputs
- ✅ **Error Handling** - Don't expose sensitive information
- ✅ **Logging** - Log security events
- ✅ **CORS Configuration** - Configure CORS properly

## 🧪 Testing

### Unit Tests

```bash
# Run security tests
./gradlew test --tests "*JwtTokenProviderTest*"
```

### Integration Tests

```bash
# Run all tests including security
./gradlew allTests
```

### Manual Testing

1. **Test Google Login**
   ```bash
   curl -X POST http://localhost:8008/api/v1/auth/google \
     -H "Content-Type: application/json" \
     -d '{"token": "your-google-id-token"}'
   ```

2. **Test Facebook Login**
   ```bash
   curl -X POST http://localhost:8008/api/v1/auth/facebook \
     -H "Content-Type: application/json" \
     -d '{"token": "your-facebook-access-token"}'
   ```

3. **Test Apple Sign In**
   ```bash
   curl -X POST http://localhost:8008/api/v1/auth/apple \
     -H "Content-Type: application/json" \
     -d '{"token": "your-apple-id-token"}'
   ```

4. **Test Protected Endpoint**
   ```bash
   curl -X GET http://localhost:8008/api/v1/user?id=1 \
     -H "Authorization: Bearer your-jwt-token"
   ```

## 🚨 Security Considerations

### Production Checklist

- [ ] **Change Default Secrets** - Update JWT_SECRET and other defaults
- [ ] **Enable HTTPS** - Configure SSL certificates
- [ ] **Configure OAuth Providers** - Set up production credentials for all providers
  - [ ] Google OAuth2 Client ID
  - [ ] Facebook App ID and Secret
  - [ ] Apple Client ID, Team ID, and Key ID
- [ ] **Database Security** - Secure database connections
- [ ] **Environment Variables** - Use secure environment variable management
- [ ] **Logging** - Configure security event logging
- [ ] **Monitoring** - Set up security monitoring

### Security Headers

The application automatically sets security headers:

- `X-Content-Type-Options: nosniff`
- `X-Frame-Options: DENY`
- `X-XSS-Protection: 1; mode=block`
- `Strict-Transport-Security: max-age=31536000; includeSubDomains`

## 🔄 Token Refresh

### Automatic Refresh

The client can refresh tokens using:

```bash
curl -X POST http://localhost:8008/api/v1/auth/refresh \
  -H "Authorization: Bearer your-current-jwt-token"
```

### Token Expiration

- **JWT Token**: 24 hours (configurable)
- **Google ID Token**: 1 hour (Google standard)
- **Facebook Access Token**: 2 hours (Facebook standard)
- **Apple ID Token**: 10 minutes (Apple standard)

## 📊 Monitoring

### Security Events to Monitor

- Failed authentication attempts
- Invalid JWT tokens
- OAuth token verification failures (Google, Facebook, Apple)
- User creation events
- Token refresh events
- Provider-specific authentication issues

### Logging

Security events are logged with appropriate levels:

- **INFO**: Successful authentication
- **WARN**: Failed authentication attempts
- **ERROR**: Security violations

## 🆘 Troubleshooting

### Common Issues

1. **OAuth Token Verification Fails**
   - **Google**: Check Google Client ID configuration, verify Google+ API is enabled
   - **Facebook**: Check Facebook App ID and Secret, verify app settings
   - **Apple**: Check Apple Client ID, Team ID, and Key ID, verify Sign in with Apple is enabled
   - Check token expiration for all providers

2. **JWT Token Issues**
   - Verify JWT secret configuration
   - Check token expiration
   - Ensure HTTPS in production

3. **CORS Issues**
   - Check CORS configuration
   - Verify allowed origins
   - Check preflight requests

4. **User Identity Issues**
   - Verify email is provided by all OAuth providers
   - Check email verification status
   - Ensure consistent user identification across providers

### Debug Mode

Enable debug logging for security components:

```properties
logging.level.com.yb.rh.security=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.com.yb.rh.security.GoogleTokenVerifier=DEBUG
logging.level.com.yb.rh.security.FacebookTokenVerifier=DEBUG
logging.level.com.yb.rh.security.AppleTokenVerifier=DEBUG
```

## 📚 Additional Resources

- [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2)
- [Facebook Login Documentation](https://developers.facebook.com/docs/facebook-login/)
- [Apple Sign In Documentation](https://developer.apple.com/documentation/sign_in_with_apple)
- [JWT.io](https://jwt.io/) - JWT token debugger
- [Spring Security Documentation](https://docs.spring.io/spring-security/site/docs/current/reference/html5/)
- [OAuth2 Security Best Practices](https://oauth.net/2/security-best-practices/) 