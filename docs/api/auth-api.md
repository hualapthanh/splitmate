# Auth API

**Version:** 1.0.0  
**Status:** Draft  
**Service:** Auth Service  
**Base Path:** `/api/v1/auth`  
**Last Updated:** 2026-07-31

---

# 1. Overview

## 1.1 Purpose

Tài liệu này định nghĩa toàn bộ REST API của Auth Service.

Auth Service chịu trách nhiệm:

- User Registration
- Authentication
- JWT Management
- Refresh Token Management
- Password Management

Toàn bộ API được expose thông qua API Gateway.

---

# 2. API Standards

## Base URL

```
/api/v1/auth
```

---

## Content Type

Request

```
application/json
```

Response

```
application/json
```

---

## Character Encoding

UTF-8

---

## Authentication

Một số endpoint yêu cầu Bearer Token.

Ví dụ

```
Authorization: Bearer <AccessToken>
```

---

## Date Format

ISO-8601 UTC

Ví dụ

```
2026-07-31T15:30:00Z
```

---

## Idempotency

| API | Idempotent |
|------|------------|
| Register | ❌ |
| Login | ❌ |
| Refresh Token | ❌ |
| Logout | ✅ |
| Change Password | ❌ |
| Get Current Account | ✅ |

---

# 3. Common Response Format

## Success Response

Mỗi endpoint sẽ trả về DTO riêng.

Không sử dụng generic wrapper.

Ví dụ:

```json
{
    "accessToken": "...",
    "refreshToken": "...",
    "expiresIn": 900,
    "tokenType": "Bearer"
}
```

---

## Error Response

Tất cả lỗi đều sử dụng cùng một format.

```json
{
    "timestamp": "2026-07-31T10:30:15Z",
    "status": 401,
    "code": "AUTH-005",
    "error": "Unauthorized",
    "message": "Invalid Access Token",
    "path": "/api/v1/auth/login",
    "traceId": "3c91ef78..."
}
```

---

# 4. Register

## Description

Đăng ký tài khoản mới.

---

## Endpoint

```
POST /api/v1/auth/register
```

---

## Authentication

Public

---

## Request DTO

RegisterRequest

```json
{
    "email": "john@example.com",
    "password": "Password@123",
    "confirmPassword": "Password@123"
}
```

---

## Validation Rules

| Field | Rule |
|---------|------|
| email | Required |
| email | Valid email format |
| email | Maximum 255 characters |
| password | Required |
| password | 8–64 characters |
| password | At least 1 uppercase |
| password | At least 1 lowercase |
| password | At least 1 number |
| password | At least 1 special character |
| confirmPassword | Must match password |

---

## Business Rules

- Email phải là duy nhất.
- Account mặc định có trạng thái **ACTIVE**.
- Role mặc định là **USER**.
- Password được mã hóa bằng BCrypt trước khi lưu.
- Sau khi tạo Account thành công, Auth Service publish sự kiện `UserRegistered`.
- User Service sẽ lắng nghe sự kiện và tạo Profile mặc định.
- Sau khi đăng ký thành công, hệ thống phát hành Access Token và Refresh Token.

---

## Processing Flow

```
Validate Request
      ↓
Check Email Exists
      ↓
Hash Password
      ↓
Create Account
      ↓
Generate JWT
      ↓
Generate Refresh Token
      ↓
Save Refresh Token
      ↓
Publish UserRegistered Event
      ↓
Return Response
```

---

## Success Response

HTTP Status

```
201 Created
```

Response DTO

```json
{
    "accountId": "8f1d8b12",
    "email": "john@example.com",
    "accessToken": "...",
    "refreshToken": "...",
    "expiresIn": 900,
    "tokenType": "Bearer"
}
```

---

## Error Codes

| Status | Code | Description |
|---------|------|-------------|
|400|AUTH-001|Validation Failed|
|409|AUTH-004|Email Already Exists|

---

## Notes

- Endpoint này không idempotent.
- Một email chỉ được đăng ký một lần.

---

# 5. Login

## Description

Xác thực người dùng và phát hành JWT.

---

## Endpoint

```
POST /api/v1/auth/login
```

Authentication

Public

---

## Request DTO

LoginRequest

```json
{
    "email": "john@example.com",
    "password": "Password@123"
}
```

---

## Business Rules

- Email phải tồn tại.
- Password phải chính xác.
- Account phải ở trạng thái ACTIVE.
- Nếu xác thực thành công, tạo mới Access Token và Refresh Token.
- Refresh Token cũ (nếu có chính sách one-session) sẽ bị thu hồi.

---

## Processing Flow

```
Validate Request
      ↓
Find Account
      ↓
Verify Password
      ↓
Check Account Status
      ↓
Generate JWT
      ↓
Generate Refresh Token
      ↓
Save Refresh Token
      ↓
Return Response
```

---

## Success Response

HTTP

```
200 OK
```

```json
{
    "accessToken": "...",
    "refreshToken": "...",
    "expiresIn": 900,
    "tokenType": "Bearer"
}
```

---

## Error Codes

| Status | Code |
|---------|------|
|400|AUTH-001|
|401|AUTH-002|
|423|AUTH-003|

---

# 6. Refresh Access Token

## Description

Sử dụng Refresh Token để cấp Access Token mới.

---

## Endpoint

```
POST /api/v1/auth/refresh
```

Authentication

Public

---

## Request DTO

RefreshTokenRequest

```json
{
    "refreshToken": "..."
}
```

---

## Business Rules

- Refresh Token phải tồn tại.
- Refresh Token chưa hết hạn.
- Refresh Token chưa bị thu hồi.
- Chỉ phát hành Access Token mới.
- Refresh Token Rotation sẽ được hỗ trợ ở Phase 2.

---

## Processing Flow

```
Validate Refresh Token
        ↓
Check Expiration
        ↓
Generate New Access Token
        ↓
Return Response
```

---

## Response

```json
{
    "accessToken": "...",
    "expiresIn": 900,
    "tokenType": "Bearer"
}
```

---

## Error Codes

| Status | Code |
|---------|------|
|401|AUTH-006|

---

# 7. Logout

## Description

Thu hồi Refresh Token hiện tại.

---

## Endpoint

```
POST /api/v1/auth/logout
```

Authentication

Bearer Token

---

## Request DTO

LogoutRequest

```json
{
    "refreshToken": "..."
}
```

---

## Business Rules

- Refresh Token sẽ được đánh dấu REVOKED.
- Sau khi Logout, Refresh Token không thể sử dụng lại.

---

## Response

```
204 No Content
```

---

## Notes

Endpoint này idempotent.

---

# 8. Change Password

## Description

Thay đổi mật khẩu tài khoản.

---

## Endpoint

```
PUT /api/v1/auth/password
```

Authentication

Bearer Token

---

## Request DTO

ChangePasswordRequest

```json
{
    "currentPassword": "Password@123",
    "newPassword": "Password@456",
    "confirmPassword": "Password@456"
}
```

---

## Business Rules

- Current Password phải chính xác.
- New Password phải khác Current Password.
- Confirm Password phải khớp.
- Password mới sẽ được BCrypt Hash.
- Sau khi đổi mật khẩu, tất cả Refresh Token hiện tại sẽ bị thu hồi.
- Publish sự kiện `PasswordChanged`.

---

## Response

```
204 No Content
```

---

## Error Codes

| Status | Code |
|---------|------|
|400|AUTH-001|
|401|AUTH-002|

---

# 9. Get Current Account

## Description

Lấy thông tin Account của người dùng đang đăng nhập.

---

## Endpoint

```
GET /api/v1/auth/me
```

Authentication

Bearer Token

---

## Response DTO

```json
{
    "id": "8f1d8b12",
    "email": "john@example.com",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2026-07-31T08:00:00Z"
}
```

---

# 10. HTTP Status Codes

| Status | Meaning |
|----------|------------|
|200|Success|
|201|Created|
|204|No Content|
|400|Bad Request|
|401|Unauthorized|
|403|Forbidden|
|404|Not Found|
|409|Conflict|
|423|Locked|
|429|Too Many Requests|
|500|Internal Server Error|

---

# 11. Error Codes

| Code | Description |
|------|-------------|
|AUTH-001|Validation Failed|
|AUTH-002|Invalid Credentials|
|AUTH-003|Account Locked|
|AUTH-004|Email Already Exists|
|AUTH-005|Invalid Access Token|
|AUTH-006|Refresh Token Expired|
|AUTH-007|Refresh Token Revoked|
|AUTH-008|Unauthorized|

---

# 12. Future APIs

Planned for future releases:

- POST /auth/forgot-password
- POST /auth/reset-password
- POST /auth/verify-email
- POST /auth/resend-verification
- POST /auth/oauth/google
- POST /auth/oauth/github
- GET /auth/devices
- DELETE /auth/devices/{deviceId}

---

# 13. References

- Auth Service Design
- Security Architecture
- API Gateway Design
- User Service Design