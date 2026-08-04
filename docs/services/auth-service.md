# Auth Service

**Version:** 1.0.0  
**Status:** Draft  
**Owner:** Backend Team  
**Last Updated:** 2026-07-31

---

# 1. Overview

## 1.1 Purpose

Auth Service chịu trách nhiệm quản lý toàn bộ quá trình xác thực và ủy quyền (Authentication & Authorization) trong hệ thống SplitMate.

Đây là service duy nhất được phép xử lý thông tin đăng nhập của người dùng như email, mật khẩu và refresh token.

Service này phát hành JWT Access Token sau khi người dùng đăng nhập thành công và xác minh Access Token cho API Gateway.

Thông tin hồ sơ cá nhân (Profile) không thuộc phạm vi của Auth Service mà được quản lý bởi User Service.

---

## 1.2 Goals

Auth Service được xây dựng nhằm đáp ứng các mục tiêu sau:

- Đăng ký tài khoản
- Đăng nhập
- Đăng xuất
- Làm mới Access Token
- Đổi mật khẩu
- Xác thực JWT
- Quản lý Refresh Token
- Mã hóa mật khẩu bằng BCrypt

---

## 1.3 Non Goals

Những chức năng sau KHÔNG thuộc Auth Service.

- Avatar
- Full Name
- Phone Number
- Date of Birth
- User Preferences
- Budget
- Expense
- Notification

---

# 2. Responsibilities

Auth Service chịu trách nhiệm:

- Create Account
- Authenticate User
- Generate JWT
- Validate JWT
- Generate Refresh Token
- Revoke Refresh Token
- Change Password
- Verify Password

Không chịu trách nhiệm:

- Quản lý Profile
- Upload Avatar
- Group Management
- Expense Management

---

# 3. Service Boundary

```
                    Auth Service

+------------------------------------------------+

 Accounts

 Refresh Tokens

 Password Hashing

 JWT Generation

 JWT Validation

 Authentication

 Authorization

+------------------------------------------------+
```

Ngoài phạm vi của service:

```
Profile
Expense
Group
Notification
Budget
Analytics
```

---

# 4. Database Ownership

Auth Service sở hữu hai bảng dữ liệu.

## accounts

Lưu thông tin xác thực.

Các trường chính

- id
- email
- password_hash
- account_status
- email_verified
- created_at
- updated_at

---

## refresh_tokens

Lưu Refresh Token.

Các trường chính

- id
- account_id
- token
- expires_at
- revoked_at
- created_at

---

# 5. Dependencies

## Infrastructure

- PostgreSQL
- Spring Boot
- Spring Security
- Spring Validation

---

## Libraries

- JWT
- BCrypt
- Lombok
- MapStruct

---

## Future

- Redis
- Kafka

---

# 6. External Communication

## Receive Request From

- API Gateway

---

## Call External Service

### User Service

Sau khi người dùng đăng ký thành công.

Auth Service publish event

```
UserRegistered
```

User Service sẽ tạo Profile.

---

## Future

Notification Service

```
Send Verification Email
```

---

# 7. API List

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | /auth/register | Register new account |
| POST | /auth/login | Login |
| POST | /auth/logout | Logout |
| POST | /auth/refresh | Refresh Access Token |
| POST | /auth/change-password | Change Password |
| GET | /auth/me | Get current account |

Chi tiết request/response sẽ được mô tả trong:

```
docs/api/auth-api.md
```

---

# 8. Authentication Flow

```
Client

↓

API Gateway

↓

Auth Service

↓

Database

↓

JWT

↓

API Gateway

↓

Client
```

---

# 9. Published Events

| Event | Description |
|--------|-------------|
| UserRegistered | New account created |
| PasswordChanged | Password updated |
| RefreshTokenRevoked | User logout |
| AccountLocked | Too many failed login attempts |

---

# 10. Consumed Events

Phase 1

None.

Future

- AccountDeleted

---

# 11. Security

## Password

BCrypt

Cost Factor

12

---

## Access Token

JWT

Expiration

15 minutes

---

## Refresh Token

Expiration

30 days

Lưu trong Database.

Có thể revoke.

---

## Authentication

Bearer Token

```
Authorization: Bearer <JWT>
```

---

## Authorization

Role Based Access Control (RBAC)

Role

- USER
- ADMIN

---

# 12. Package Structure

```
auth-service
│
├── config
├── controller
├── dto
│   ├── request
│   └── response
├── entity
├── repository
├── service
│   ├── impl
│   └── mapper
├── security
│   ├── jwt
│   ├── filter
│   └── provider
├── event
├── exception
├── validation
└── util
```

---

# 13. Configuration

Ví dụ.

```yaml
jwt:
  secret: ${JWT_SECRET}
  access-token-expiration: 900
  refresh-token-expiration: 2592000

security:
  bcrypt-strength: 12
```

---

# 14. Error Codes

| Code | Description |
|------|-------------|
| AUTH-001 | Invalid Email |
| AUTH-002 | Invalid Password |
| AUTH-003 | Account Locked |
| AUTH-004 | Email Already Exists |
| AUTH-005 | Invalid JWT |
| AUTH-006 | Refresh Token Expired |
| AUTH-007 | Unauthorized |

---

# 15. Design Decisions (ADR)

## ADR-001

### Tại sao dùng JWT?

JWT cho phép Authentication theo mô hình Stateless.

Điều này phù hợp với kiến trúc Microservice vì không cần lưu Session trên Server.

---

## ADR-002

### Tại sao Access Token chỉ sống 15 phút?

Nếu Access Token bị đánh cắp, thời gian khai thác sẽ bị giới hạn.

Refresh Token sẽ chịu trách nhiệm cấp lại Access Token mới.

---

## ADR-003

### Tại sao Refresh Token lưu Database?

Cho phép:

- Logout
- Force Logout
- Revoke Token
- Device Management trong tương lai

---

## ADR-004

### Tại sao Profile không nằm trong Auth Service?

Auth Service chỉ chịu trách nhiệm Authentication.

Thông tin cá nhân thuộc User Service.

Điều này tuân theo nguyên tắc Single Responsibility và giúp việc mở rộng hệ thống dễ dàng hơn.

---

# 16. Future Enhancements

- Google OAuth2
- GitHub OAuth2
- Multi-Factor Authentication (MFA)
- Email Verification
- Password Reset
- Device Management
- Login History
- Suspicious Login Detection

---

# 17. Open Questions

Các vấn đề sẽ được quyết định ở các Sprint sau.

- Có cho phép đăng nhập bằng Username không?
- Có giới hạn số lượng thiết bị đăng nhập không?
- Có sử dụng Redis để lưu Blacklist Token không?
- Có hỗ trợ Social Login không?

---

# 18. References

- BRD
- SRS
- Database Schema
- API Contract
- Sequence Diagram