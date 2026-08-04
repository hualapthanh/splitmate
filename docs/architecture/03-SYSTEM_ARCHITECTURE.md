# System Architecture

**Project:** SplitMate  
**Version:** 1.0.0  
**Status:** Draft  
**Author:** Backend Team  
**Last Updated:** 2026-08-01

---

# 1. Overview

## 1.1 Purpose

Tài liệu này mô tả kiến trúc tổng thể của hệ thống SplitMate.

Mục tiêu của tài liệu:

- Giúp Developer hiểu kiến trúc hệ thống.
- Là tài liệu onboarding cho thành viên mới.
- Là nguồn tham chiếu cho AI Agent khi sinh mã nguồn.
- Định nghĩa các nguyên tắc thiết kế và giao tiếp giữa các service.

---

# 2. Project Overview

SplitMate là nền tảng quản lý chi tiêu cá nhân và nhóm.

Hệ thống cho phép người dùng:

- Quản lý chi tiêu cá nhân.
- Quản lý nhóm chia tiền.
- Tính toán số dư giữa các thành viên.
- Theo dõi ngân sách.
- Đề xuất phương án thanh toán tối ưu.
- Phân tích thói quen chi tiêu.

---

# 3. Architecture Style

SplitMate sử dụng kiến trúc **Microservices Architecture**.

Mỗi service:

- Có trách nhiệm nghiệp vụ riêng.
- Có database riêng.
- Có vòng đời triển khai độc lập.
- Có thể mở rộng độc lập.

Tất cả request từ client đều đi qua API Gateway.

---

# 4. High-Level Architecture

```text
                +-------------------+
                |  Web / Mobile UI  |
                +---------+---------+
                          |
                          |
                 HTTPS / REST API
                          |
                          v
                +-------------------+
                |    API Gateway    |
                +---------+---------+
                          |
        +-----------------+-----------------+
        |                 |                 |
        v                 v                 v
+---------------+ +---------------+ +----------------+
| Auth Service  | | User Service  | | Group Service  |
+---------------+ +---------------+ +----------------+
        |                 |                 |
        +-----------------+-----------------+
                          |
                          |
                +------------------------+
                |     Kafka (Events)     |
                +------------------------+
                          |
        +-----------------+-------------------------------+
        |                 |               |               |
        v                 v               v               v
+---------------+ +---------------+ +---------------+ +------------------+
| Expense       | | Balance       | | Budget        | | Notification     |
| Service       | | Service       | | Service       | | Service          |
+---------------+ +---------------+ +---------------+ +------------------+
                          |
                          |
                    +-------------+
                    | PostgreSQL  |
                    +-------------+
```

---

# 5. Technology Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Architecture | Microservices |
| Build Tool | Maven |
| API | RESTful API |
| Security | Spring Security + JWT |
| Database | PostgreSQL |
| Cache | Redis |
| Messaging | Apache Kafka |
| Gateway | Spring Cloud Gateway |
| Object Mapping | MapStruct |
| Validation | Jakarta Validation |
| Documentation | OpenAPI + Swagger |
| Containerization | Docker |
| CI/CD | GitHub Actions (Planned) |
| Monitoring | Prometheus + Grafana (Planned) |

---

# 6. Services Overview

## API Gateway

Responsibilities

- Single Entry Point.
- Request Routing.
- Authentication Filter.
- Rate Limiting.
- Logging.
- Request Validation.

---

## Auth Service

Responsibilities

- Registration.
- Login.
- JWT Generation.
- Refresh Token.
- Password Management.

Database

Auth Database

---

## User Service

Responsibilities

- User Profile.
- Avatar.
- Personal Information.
- Preferences.

Database

User Database

---

## Group Service

Responsibilities

- Create Group.
- Invite Member.
- Group Roles.
- Group Settings.

Database

Group Database

---

## Expense Service

Responsibilities

- Expense Management.
- Expense Split.
- Attachments.
- Categories.

Database

Expense Database

---

## Balance Service

Responsibilities

- Calculate Balance.
- Debt Tracking.
- Settlement Suggestion.

Database

Balance Database

---

## Budget Service

Responsibilities

- Budget Management.
- Spending Limit.
- Monthly Budget.

Database

Budget Database

---

## Notification Service

Responsibilities

- Email Notification.
- Push Notification.
- Event Notification.

Database

Notification Database

---

## Analytics Service

Responsibilities

- Spending Statistics.
- Charts.
- Reports.
- Insights.

Database

Analytics Database

---

# 7. Communication

SplitMate sử dụng hai hình thức giao tiếp.

## 7.1 Synchronous Communication

REST API

Ví dụ

```
Gateway
    ↓
User Service
```

---

## 7.2 Asynchronous Communication

Kafka Event

Ví dụ

```
Auth Service

↓

UserRegistered Event

↓

Kafka

↓

User Service

↓

Create Default Profile
```

---

# 8. Authentication Flow

```text
Client
    |
    | Login
    v
API Gateway
    |
    v
Auth Service
    |
    | Validate Credential
    |
    | Generate JWT
    |
    +----------------------+
                           |
                      Access Token
                           |
                           v
Client
```

Sau khi đăng nhập thành công:

- Access Token được gửi trong Authorization Header.
- API Gateway xác thực JWT trước khi chuyển tiếp request.
- Service phía sau không cần thực hiện đăng nhập lại.

---

# 9. Database Strategy

SplitMate áp dụng nguyên tắc:

> **Database per Service**

Mỗi service sở hữu database của riêng mình.

Không service nào được truy cập trực tiếp database của service khác.

Ví dụ

```
Auth Service
    |
    +---- Auth Database

User Service
    |
    +---- User Database

Expense Service
    |
    +---- Expense Database
```

Trao đổi dữ liệu giữa các service phải thông qua:

- REST API
- Kafka Event

---

# 10. Security Architecture

Authentication

- JWT

Authorization

- Role-Based Access Control (RBAC)

Password

- BCrypt

Transport

- HTTPS

Token Validation

- API Gateway

---

# 11. Design Principles

SplitMate tuân theo các nguyên tắc sau:

- Single Responsibility Principle.
- Separation of Concerns.
- Database per Service.
- Event-Driven Architecture.
- Stateless Services.
- API First Design.
- DTO-based Communication.
- Layered Architecture.

---

# 12. Scalability

Kiến trúc được thiết kế để hỗ trợ:

- Horizontal Scaling.
- Container Deployment.
- Independent Deployment.
- Service Isolation.
- Fault Isolation.

---

# 13. Future Improvements

Các thành phần dự kiến bổ sung:

- Service Discovery.
- Config Server.
- Distributed Tracing.
- ELK Stack.
- Prometheus.
- Grafana.
- OpenTelemetry.
- Kubernetes Deployment.

---

# 14. References

- BRD
- SRS
- Database Schema
- Security Architecture
- Service Design Documents