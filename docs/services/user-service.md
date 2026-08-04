# User Service

**Version:** 1.0.0  
**Status:** Draft  
**Owner:** Backend Team  
**Last Updated:** 2026-07-31

---

# 1. Overview

## 1.1 Purpose

User Service chịu trách nhiệm quản lý thông tin cá nhân và cấu hình người dùng trong hệ thống SplitMate.

Service này lưu trữ và cập nhật dữ liệu hồ sơ (Profile) như tên hiển thị, avatar, múi giờ, ngôn ngữ và các thiết lập cá nhân khác.

User Service không xử lý xác thực (Authentication), mật khẩu, JWT hoặc Refresh Token. Các chức năng đó thuộc về Auth Service.

---

## 1.2 Goals

User Service được xây dựng nhằm đáp ứng các mục tiêu sau:

- Tạo hồ sơ người dùng sau khi đăng ký
- Lấy thông tin hồ sơ cá nhân
- Cập nhật thông tin cá nhân
- Cập nhật avatar
- Cập nhật múi giờ (timezone)
- Cập nhật ngôn ngữ (locale)
- Lấy thông tin hồ sơ công khai của người dùng khác

---

## 1.3 Non Goals

Những chức năng sau KHÔNG thuộc User Service.

- Đăng ký tài khoản
- Đăng nhập
- Đổi mật khẩu
- Refresh Token
- JWT
- Quản lý nhóm
- Quản lý khoản chi
- Quản lý ngân sách
- Gửi thông báo

---

# 2. Responsibilities

User Service chịu trách nhiệm:

- Create Profile
- Get Current User Profile
- Update Profile
- Update Avatar
- Update Preferences
- Get Public Profile

Không chịu trách nhiệm:

- Verify Password
- Generate JWT
- Authenticate User
- Manage Refresh Token

---

# 3. Service Boundary

                    User Service

+------------------------------------------------+

Profiles

Avatar

Timezone

Locale

User Preferences

Public Profile

+------------------------------------------------+

Ngoài phạm vi của service:

Password
JWT
Expense
Group
Budget
Notification

---

# 4. Database Ownership

User Service sở hữu bảng dữ liệu sau.

## profiles

Lưu thông tin hồ sơ người dùng.

Các trường chính

- user_id
- full_name
- avatar_url
- phone_number
- bio
- timezone
- locale
- created_at
- updated_at

---

# 5. Dependencies

## Infrastructure

- PostgreSQL
- Spring Boot
- Spring Web
- Spring Validation

---

## Libraries

- Lombok
- MapStruct

---

## Future

- Cloudinary
- S3 Compatible Storage
- Kafka

---

# 6. External Communication

## Receive Request From

- API Gateway

---

## Call External Service

Phase 1

None.

---

## Consume Event From Auth Service

### UserRegistered

Khi người dùng đăng ký thành công, Auth Service publish event:

UserRegistered

User Service sẽ tạo bản ghi Profile mặc định.

---

# 7. API List

| Method | Endpoint | Description |
|---|---|---|
| GET | /users/me | Get current user profile |
| PUT | /users/me | Update current user profile |
| PUT | /users/avatar | Update avatar |
| GET | /users/{id} | Get public profile |

Chi tiết request/response sẽ được mô tả trong:

docs/api/user-api.md

---

# 8. Profile Lifecycle

UserRegistered Event
↓
Create Default Profile
↓
User Updates Profile
↓
User Updates Avatar
↓
Public Profile Available

---

# 9. Published Events

| Event | Description |
|---|---|
| ProfileCreated | Default profile created |
| ProfileUpdated | Profile updated |
| AvatarUpdated | Avatar changed |

---

# 10. Consumed Events

| Event | Source |
|---|---|
| UserRegistered | Auth Service |

---

# 11. Security

## Authentication

Tất cả endpoint trừ `GET /users/{id}` yêu cầu JWT hợp lệ.

---

## Authorization

Người dùng chỉ được phép cập nhật hồ sơ của chính mình.

---

## Avatar Upload

- Chỉ chấp nhận image/*
- Giới hạn kích thước 5 MB
- Quét virus (Future)

---

# 12. Package Structure

user-service
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
├── event
├── exception
├── validation
└── util

---

# 13. Configuration

Ví dụ.

```yaml
app:
  avatar:
    max-size: 5MB
    allowed-types:
      - image/jpeg
      - image/png
      - image/webp
```

---

# 14. Error Codes

| Code | Description |
|---|---|
| USER-001 | Profile Not Found |
| USER-002 | Invalid Avatar |
| USER-003 | Avatar Too Large |
| USER-004 | Invalid Timezone |
| USER-005 | Invalid Locale |
| USER-006 | Unauthorized |

---

# 15. Design Decisions (ADR)

## ADR-001

### Tại sao tách Profile khỏi Account?

Profile thay đổi thường xuyên, trong khi Account là dữ liệu xác thực nhạy cảm.

Tách riêng giúp:

- Giảm coupling
- Dễ mở rộng User Service
- Dễ phân quyền
- Dễ tách database sau này

---

## ADR-002

### Tại sao dùng `user_id` làm Primary Key?

`user_id` chính là định danh toàn cục của người dùng trong hệ thống.

Điều này giúp mapping giữa các service đơn giản hơn và tránh phải duy trì thêm một profile_id không cần thiết.

---

## ADR-003

### Tại sao không lưu avatar dạng BLOB?

Lưu file trong database làm tăng kích thước database và ảnh hưởng hiệu năng backup.

Chỉ lưu `avatar_url`, còn file được lưu ở object storage.

---

# 16. Future Enhancements

- Multiple Avatars
- Avatar History
- User Preferences Table
- Dark Mode Preference
- Notification Preference
- Privacy Settings
- Search User
- Follow/Friend Feature (Future Product Direction)

---

# 17. Open Questions

Các vấn đề sẽ được quyết định ở các Sprint sau.

- Có cho phép ẩn số điện thoại không?
- Có cho phép profile private không?
- Có hỗ trợ nhiều ngôn ngữ hiển thị không?
- Có cần username duy nhất không?

---

# 18. References

- BRD
- SRS
- Database Schema
- Auth Service Design
- API Contract
- Sequence Diagram