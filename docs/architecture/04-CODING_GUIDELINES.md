# Coding Guidelines

**Project:** SplitMate  
**Version:** 1.0.0  
**Status:** Active  
**Last Updated:** 2026-08-01

---

# 1. Purpose

Tài liệu này định nghĩa các quy tắc phát triển phần mềm trong dự án SplitMate.

Mục tiêu:

- Đảm bảo toàn bộ source code có cùng phong cách.
- Giúp AI Agent sinh code nhất quán.
- Giúp Developer mới dễ dàng onboard.
- Tăng khả năng maintain và review code.

---

# 2. General Principles

Mọi thành viên trong dự án phải tuân thủ các nguyên tắc sau:

- Clean Code
- SOLID Principles
- DRY (Don't Repeat Yourself)
- KISS (Keep It Simple)
- YAGNI (You Aren't Gonna Need It)

---

# 3. Project Structure

Mỗi service phải có cấu trúc như sau:

```

src/main/java

└── com.splitmate.auth

├── config

├── controller

├── service

│ ├── impl

├── repository

├── entity

├── dto

│ ├── request

│ ├── response

├── mapper

├── security

├── exception

├── event

├── listener

└── util

```

Không được tạo package tùy ý.

---

# 4. Layer Responsibilities

## Controller

Chỉ có trách nhiệm:

- Nhận request
- Validate request
- Gọi Service
- Trả Response

Không được:

- Viết Business Logic
- Query Database
- Mapping Entity

---

## Service

Service chịu trách nhiệm:

- Business Logic
- Transaction
- Publish Event

---

## Repository

Chỉ dùng để thao tác Database.

Không chứa Business Logic.

---

## Mapper

Sử dụng MapStruct.

Không mapping thủ công nếu có thể.

---

# 5. Dependency Injection

Chỉ sử dụng Constructor Injection.

Đúng

```java
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository repository;
}
```

Sai

```java
@Autowired
private UserRepository repository;
```

Không sử dụng Field Injection.

---

# 6. DTO Rules

Không bao giờ return Entity.

Đúng

```
Entity

↓

Mapper

↓

Response DTO
```

Sai

```
Controller

↓

Return Entity
```

Mọi Request đều sử dụng Request DTO.

Mọi Response đều sử dụng Response DTO.

---

# 7. Validation

Sử dụng Jakarta Validation.

Ví dụ

```java
@NotBlank
@Email
private String email;
```

Không validate thủ công trong Controller.

---

# 8. Exception Handling

Toàn bộ Exception phải được xử lý bởi:

```
GlobalExceptionHandler
```

Không dùng

```java
try {

}
catch(Exception e){

}
```

trong Controller.

---

# 9. Logging

Sử dụng SLF4J.

Ví dụ

```java
log.info("User {} logged in", email);
```

Không log:

- Password
- JWT
- Refresh Token
- Thông tin nhạy cảm

---

# 10. Naming Convention

## Class

PascalCase

Ví dụ

```
UserService

ExpenseController

BudgetRepository
```

---

## Method

camelCase

```
createExpense()

findUser()

calculateBalance()
```

---

## Variable

camelCase

```
userId

groupId

totalAmount
```

---

## Constant

UPPER_SNAKE_CASE

```
ACCESS_TOKEN_EXPIRE_TIME

MAX_GROUP_SIZE
```

---

## Package

lowercase

```
controller

service

repository
```

---

# 11. REST API Guidelines

Ví dụ

Đúng

```
GET /users/{id}

POST /groups

PUT /profile

DELETE /expenses/{id}
```

Sai

```
GET /getUser

POST /createExpense

POST /deleteExpense
```

---

# 12. Transaction Management

Chỉ Service được sử dụng

```
@Transactional
```

Không đặt Transaction trong Controller.

---

# 13. Entity Guidelines

Entity chỉ dùng cho Persistence.

Không:

- JSON Logic
- Business Logic
- Validation

Ví dụ

```
Entity

↓

Repository

↓

Service

↓

Mapper

↓

DTO
```

---

# 14. Mapper

Bắt buộc sử dụng MapStruct.

Không tự viết:

```
dto.setName(entity.getName())
```

trừ khi có logic đặc biệt.

---

# 15. API Response

Tất cả Response phải sử dụng Response DTO.

Ví dụ

```json
{
  "id": "...",
  "email": "...",
  "createdAt": "..."
}
```

Không trả về Entity.

---

# 16. Security

Không lưu:

- Password dạng plain text.
- JWT vào Database.
- Secret Key trong source code.

Mọi Secret phải nằm trong:

- Environment Variables
- Docker Secrets (Future)

---

# 17. Database Rules

Mỗi Service chỉ được truy cập Database của chính nó.

Không được:

```
Expense Service

↓

Query User Database
```

Thay vào đó:

- REST API
- Kafka Event

---

# 18. Event Guidelines

Khi thay đổi dữ liệu quan trọng:

- Publish Domain Event.

Ví dụ

```
UserRegistered

PasswordChanged

ExpenseCreated

SettlementCompleted
```

Không gọi trực tiếp Service khác nếu có thể dùng Event.

---

# 19. Testing

Mỗi Service phải có:

- Unit Test
- Integration Test

Không merge code khi chưa pass test.

---

# 20. Code Review Checklist

Trước khi tạo Pull Request:

- Không còn TODO.
- Không còn Comment thừa.
- Không còn Debug Log.
- Không return Entity.
- Validation đầy đủ.
- Exception được xử lý.
- API đúng Contract.
- Test đã pass.

---

# 21. AI Agent Instructions

AI Agent phải tuân thủ:

- Không tự ý thay đổi API Contract.
- Không tự ý sửa Database Schema.
- Không return Entity.
- Không dùng Field Injection.
- Luôn sử dụng DTO.
- Luôn dùng Constructor Injection.
- Luôn dùng MapStruct.
- Business Logic chỉ nằm trong Service.
- Không truy cập Database của Service khác.

Nếu không chắc chắn về thiết kế, ưu tiên tài liệu trong thư mục `docs/`.

---

# 22. References

- System Architecture
- Security Architecture
- Database Schema
- API Contracts
- Service Design Documents
