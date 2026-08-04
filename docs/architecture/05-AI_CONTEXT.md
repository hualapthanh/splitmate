# AI Context

**Project:** SplitMate  
**Version:** 1.0.0  
**Audience:** AI Agents (Cursor, Claude Code, GitHub Copilot, Gemini CLI, ChatGPT...)

---

# 1. Project Summary

SplitMate là một nền tảng quản lý chi tiêu cá nhân và nhóm.

Mục tiêu của hệ thống:

- Quản lý chi tiêu cá nhân.
- Chia tiền giữa các thành viên.
- Theo dõi công nợ.
- Quản lý ngân sách.
- Thống kê chi tiêu.
- Đề xuất phương án thanh toán tối ưu.

Đây là dự án học tập theo tiêu chuẩn Enterprise Software Development.

AI Agent phải ưu tiên tuân thủ tài liệu thiết kế thay vì tự suy luận.

---

# 2. Tech Stack

| Category | Technology |
|-----------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Build Tool | Maven |
| Database | PostgreSQL |
| Cache | Redis |
| Messaging | Apache Kafka |
| Security | Spring Security + JWT |
| Gateway | Spring Cloud Gateway |
| Object Mapping | MapStruct |
| Validation | Jakarta Validation |
| Documentation | OpenAPI 3 |
| Container | Docker |
| Testing | JUnit 5 + Mockito + Testcontainers |

---

# 3. Architecture

Kiến trúc:

Microservices Architecture.

Mỗi service:

- Có database riêng.
- Có business riêng.
- Không truy cập database của service khác.

Communication:

- REST
- Kafka Event

---

# 4. Services

## API Gateway

Single Entry Point.

Không chứa business logic.

---

## Auth Service

Quản lý:

- Account
- Login
- Register
- JWT
- Refresh Token
- Password

Không quản lý Profile.

---

## User Service

Quản lý:

- User Profile
- Avatar
- Preferences

Không quản lý Password.

---

## Group Service

Quản lý:

- Group
- Member
- Invitation

---

## Expense Service

Quản lý:

- Expense
- Split
- Category
- Receipt

---

## Balance Service

Quản lý:

- Debt
- Balance
- Settlement Suggestion

---

## Budget Service

Quản lý:

- Monthly Budget
- Spending Limit

---

## Notification Service

Quản lý:

- Email
- Push Notification

---

## Analytics Service

Quản lý:

- Reports
- Charts
- Spending Analysis

---

# 5. Design Principles

SplitMate tuân thủ:

- SOLID
- Clean Architecture
- Layered Architecture
- API First
- Database per Service
- Event Driven
- DTO Based Communication

---

# 6. Database Rules

Mỗi service có database riêng.

Không được:

Expense Service

↓

Query User Database

Thay vào đó:

REST

hoặc

Kafka Event

---

# 7. Security Rules

Authentication

JWT

Authorization

RBAC

Password

BCrypt

Secret

Environment Variables

Không hardcode Secret Key.

---

# 8. API Rules

RESTful API.

Ví dụ:

GET /users/{id}

POST /groups

PUT /profile

DELETE /expenses/{id}

Không sử dụng:

GET /getUser

POST /createExpense

---

# 9. DTO Rules

Không return Entity.

Controller luôn trả về Response DTO.

Request luôn sử dụng Request DTO.

Entity chỉ dùng để Persistence.

---

# 10. Coding Rules

Constructor Injection.

MapStruct.

Validation.

Global Exception Handler.

Không sử dụng Field Injection.

Không viết Business Logic trong Controller.

---

# 11. Event Rules

Các sự kiện chính:

UserRegistered

PasswordChanged

ExpenseCreated

ExpenseUpdated

ExpenseDeleted

SettlementCompleted

BudgetExceeded

---

# 12. Important Constraints

AI Agent KHÔNG được:

- Truy cập Database của service khác.
- Trả về Entity.
- Hardcode Secret.
- Viết Business Logic trong Controller.
- Tự ý thay đổi API Contract.
- Tự ý thay đổi Database Schema.

---

# 13. Development Workflow

Khi implement một feature:

1. Đọc Service Design.
2. Đọc API Contract.
3. Đọc Database Schema.
4. Đọc Security Architecture.
5. Sau đó mới sinh code.

Nếu có mâu thuẫn, ưu tiên:

Database Schema

↓

API Contract

↓

Service Design

↓

AI Context

---

# 14. Documents

AI Agent nên đọc theo thứ tự:

SYSTEM_ARCHITECTURE.md

↓

CODING_GUIDELINES.md

↓

DATABASE_SCHEMA.md

↓

SECURITY_ARCHITECTURE.md

↓

Service Design

↓

API Contract

---

# 15. Goal

Mục tiêu của AI Agent:

- Sinh code đúng kiến trúc.
- Không phá vỡ thiết kế.
- Không tự ý thêm framework.
- Tuân thủ toàn bộ Coding Guidelines.
- Viết code dễ test.
- Viết code dễ maintain.