# Security Architecture

**Version:** 1.0.0  
**Status:** Draft  
**Owner:** Backend Team  
**Last Updated:** 2026-07-31

---

# 1. Overview

## 1.1 Purpose

Tài liệu này mô tả kiến trúc bảo mật của hệ thống SplitMate.

Mục tiêu là định nghĩa thống nhất cơ chế Authentication, Authorization, Token Management và Security Policy cho tất cả Microservices.

Tất cả các service phải tuân thủ tài liệu này.

---

# 2. Security Objectives

Hệ thống cần đảm bảo:

- Authentication
- Authorization
- Confidentiality
- Integrity
- Availability
- Auditability

---

# 3. Security Architecture

```

+------------+
| Client |
+------------+
|
| HTTPS
▼
+----------------+
| API Gateway |
+----------------+
|
| Validate JWT
|
▼
+---------------------+
| Microservices |
+---------------------+
|
▼
+-------------+
| PostgreSQL |
+-------------+

```

Gateway là điểm duy nhất tiếp nhận request từ bên ngoài.

Các Microservice không expose trực tiếp ra Internet.

---

# 4. Authentication

SplitMate sử dụng:

JWT Bearer Authentication

```

Authorization: Bearer \<AccessToken>

```

Authentication được thực hiện tại API Gateway.

Gateway sẽ:

- Parse JWT
- Validate Signature
- Validate Expiration
- Validate Issuer

Nếu hợp lệ:

Forward request.

Nếu không hợp lệ:

HTTP 401 Unauthorized

---

# 5. Authorization

Phase 1

Role Based Access Control (RBAC)

Các Role hiện tại

| Role | Description |
|------|-------------|
| USER | Normal User |
| ADMIN | System Administrator |

Mỗi service có thể định nghĩa Permission riêng.

Ví dụ

Expense Service

```

expense:create
expense:update
expense:delete

```

---

# 6. JWT Design

## Access Token

Purpose

Authentication

Lifetime

15 minutes

Storage

Memory (Frontend)

Payload

```

{
"sub":"account-id",

    "email":"user@email.com",

    "role":"USER",

    "iat":...,

    "exp":...

}

```

---

## Refresh Token

Purpose

Generate new Access Token

Lifetime

30 days

Storage

Database

Revocable

YES

Rotation

Future

---

# 7. Password Policy

Password được Hash bằng BCrypt.

Strength

12

Yêu cầu:

- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one number
- At least one special character

Password không bao giờ được lưu dạng Plain Text.

---

# 8. HTTPS

Toàn bộ request phải sử dụng HTTPS.

HTTP sẽ bị Redirect.

Cookie (Future)

```

Secure

HttpOnly

SameSite=Lax

```

---

# 9. Authentication Flow

```

Client

│

│ Login

▼

Gateway

│

▼

Auth Service

│

▼

Database

│

▼

Generate JWT

│

▼

Gateway

│

▼

Client

```

---

# 10. Authorization Flow

```

Client

│ GET /expenses

▼

Gateway

│

│ Validate JWT

▼

Expense Service

│

│ Check Ownership

▼

Database

│

▼

Response

```

Gateway chỉ xác thực.

Permission được kiểm tra tại Service.

---

# 11. Token Lifecycle

```

Issue

↓

Active

↓

Expired

↓

Refresh

↓

New Access Token

```

Logout

↓

Refresh Token Revoked

↓

Cannot Refresh Again

---

# 12. Error Response

Tất cả lỗi Authentication sử dụng format thống nhất.

```json
{
  "timestamp": "2026-07-31T10:00:00Z",
  "status": 401,
  "code": "AUTH-005",
  "error": "Unauthorized",
  "message": "Invalid Access Token",
  "path": "/api/v1/users/me",
  "traceId": "6d1dfe45..."
}
```

---

# 13. Security Headers

Gateway sẽ thêm các Header:

```

X-Content-Type-Options: nosniff

X-Frame-Options: DENY

Referrer-Policy: no-referrer

Content-Security-Policy

```

Future

```

Permissions-Policy

Strict-Transport-Security

```

---

# 14. Rate Limiting

Phase 2

Anonymous User

30 requests/minute

Authenticated User

300 requests/minute

Admin

1000 requests/minute

Redis sẽ được sử dụng để lưu Counter.

---

# 15. Audit Logging

Các hành động sau phải được ghi Audit.

- Login Success
- Login Failed
- Register
- Password Change
- Logout
- Account Locked

Không ghi:

- Password
- JWT
- Refresh Token

---

# 16. Trace ID

Gateway tạo:

```

X-Trace-Id

```

Ví dụ

```

f1d9f8b8-2d6b-4f65-bc3b-9bde...

```

Tất cả service phải ghi Trace ID vào Log.

---

# 17. Security Threats

Các nguy cơ chính

- Brute Force Attack
- Token Theft
- Replay Attack
- SQL Injection
- XSS
- CSRF (Future Web)
- DoS

Biện pháp

- BCrypt
- HTTPS
- JWT Expiration
- Input Validation
- Rate Limiting
- Parameterized Query

---

# 18. Design Decisions (ADR)

## ADR-001

### Tại sao dùng JWT thay vì Session?

JWT giúp hệ thống Stateless.

Phù hợp với Microservice và Horizontal Scaling.

---

## ADR-002

### Tại sao Access Token chỉ sống 15 phút?

Giảm rủi ro khi Token bị đánh cắp.

Refresh Token chịu trách nhiệm cấp Token mới.

---

## ADR-003

### Tại sao Refresh Token lưu Database?

Cho phép:

- Logout
- Force Logout
- Device Management
- Token Revocation

---

## ADR-004

### Tại sao Gateway chỉ Authentication?

Gateway chỉ xác minh người dùng đã đăng nhập.

Business Authorization được giao cho từng Service để tránh Gateway trở thành nơi chứa toàn bộ logic phân quyền.

---

## ADR-005

### Tại sao dùng BCrypt?

BCrypt có Salt tích hợp và chống Rainbow Table Attack.

Đây là thuật toán được khuyến nghị cho việc lưu trữ mật khẩu.

---

# 19. Future Enhancements

- OAuth2
- Google Login
- GitHub Login
- Multi-Factor Authentication (MFA)
- Email Verification
- Password Reset
- Device Management
- Suspicious Login Detection
- Session Management
- API Key Authentication

---

# 20. References

- BRD
- SRS
- System Architecture
- Auth Service Design
- User Service Design
- API Gateway Design