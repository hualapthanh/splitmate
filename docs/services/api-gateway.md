# API Gateway

**Version:** 1.0.0  
**Status:** Draft  
**Owner:** Backend Team  
**Last Updated:** 2026-07-31

---

# 1. Overview

## 1.1 Purpose

API Gateway là điểm truy cập duy nhất (Single Entry Point) của toàn bộ hệ thống SplitMate.

Mọi request từ Client (Web, Mobile hoặc các ứng dụng bên thứ ba) đều phải đi qua API Gateway trước khi được định tuyến đến các Microservices tương ứng.

Gateway chịu trách nhiệm định tuyến request, xác thực Access Token, thực thi các chính sách bảo mật chung và xử lý các concern dùng chung giữa các service.

Gateway **không chứa business logic**.

---

## 1.2 Goals

API Gateway được xây dựng nhằm:

- Cung cấp một Entry Point duy nhất cho Client
- Định tuyến request đến đúng Microservice
- Xác thực JWT Access Token
- Thực hiện Authorization cơ bản
- Cấu hình CORS
- Áp dụng Rate Limiting
- Ghi log request
- Theo dõi Trace ID
- Chuẩn hóa Error Response

---

## 1.3 Non Goals

Gateway KHÔNG thực hiện:

- Login
- Register
- Quản lý User
- Quản lý Expense
- Quản lý Group
- Truy cập Database
- Chứa Business Logic

Gateway chỉ làm nhiệm vụ chuyển tiếp và kiểm soát request.

---

# 2. Responsibilities

Gateway chịu trách nhiệm:

- Route Request
- Validate JWT
- Forward Request
- Global Exception Handling
- Request Logging
- Rate Limiting
- CORS
- Security Filter
- Correlation ID
- Request Tracing

---

# 3. Service Boundary

```
                 API Gateway

+------------------------------------------------+

Routing

Authentication

Authorization

JWT Validation

Rate Limiting

CORS

Logging

Request Tracing

Global Exception

+------------------------------------------------+
```

Không thuộc Gateway:

```
Business Logic

Database

Expense Calculation

User Profile

Budget

Notification
```

---

# 4. Routing

Gateway sẽ định tuyến request theo Path Prefix.

| Path | Destination |
|-------|-------------|
| /api/v1/auth/** | Auth Service |
| /api/v1/users/** | User Service |
| /api/v1/groups/** | Group Service |
| /api/v1/expenses/** | Expense Service |
| /api/v1/budgets/** | Budget Service |
| /api/v1/notifications/** | Notification Service |
| /api/v1/analytics/** | Analytics Service |

Gateway không biết business logic của các endpoint.

---

# 5. Dependencies

## Infrastructure

- Spring Boot
- Spring Cloud Gateway
- Spring Security

---

## Libraries

- JWT
- Lombok
- Actuator

---

## Future

- Redis
- OpenTelemetry
- Zipkin
- Prometheus

---

# 6. Authentication

Gateway sử dụng JWT Bearer Token.

Ví dụ:

Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

Quy trình:

1. Client gửi Access Token.
2. Gateway kiểm tra Token.
3. Token hợp lệ → chuyển tiếp request.
4. Token không hợp lệ → trả về HTTP 401.

Gateway không kiểm tra Password.

---

# 7. Authorization

Phase 1

Gateway chỉ kiểm tra:

- Đã đăng nhập hay chưa.

Các quyền chi tiết (Role, Permission) sẽ được xử lý tại từng Service.

Future:

- RBAC
- Permission-based Authorization

---

# 8. Route Configuration

Ví dụ:

```
/api/v1/auth/**

↓

Auth Service
```

```
/api/v1/users/**

↓

User Service
```

```
/api/v1/groups/**

↓

Group Service
```

Gateway sẽ cấu hình thông qua Spring Cloud Gateway Route Locator.

---

# 9. Request Flow

```
Client

↓

API Gateway

↓

JWT Validation

↓

Route Matching

↓

Forward Request

↓

Target Service

↓

Response

↓

Gateway

↓

Client
```

---

# 10. Error Handling

Gateway chuẩn hóa Error Response.

Ví dụ:

```json
{
    "timestamp": "2026-07-31T10:30:15Z",
    "status": 401,
    "error": "Unauthorized",
    "code": "AUTH-005",
    "message": "Invalid Access Token",
    "path": "/api/v1/users/me",
    "traceId": "9f9cb4b4..."
}
```

Gateway sẽ không trả Stack Trace cho Client.

---

# 11. Logging

Gateway ghi lại:

- HTTP Method
- URI
- Response Status
- Response Time
- Trace ID
- Client IP

Không ghi:

- Password
- JWT
- Refresh Token

---

# 12. Request Tracing

Gateway tạo Correlation ID (Trace ID) cho mỗi request.

Ví dụ:

```
X-Trace-Id:
9f9cb4b49c...
```

Gateway sẽ truyền Trace ID đến tất cả Microservices thông qua Header.

Điều này giúp theo dõi toàn bộ vòng đời của request trong hệ thống.

---

# 13. Rate Limiting

Future (Phase 2)

Giới hạn số lượng request nhằm chống Spam.

Ví dụ:

- Anonymous User:
    - 30 request/phút

- Authenticated User:
    - 300 request/phút

Rate Limiting sẽ được lưu trên Redis.

---

# 14. CORS

Gateway là nơi cấu hình CORS tập trung.

Cho phép:

- Web Frontend
- Mobile Application

Không cho phép Origin không nằm trong Whitelist.

---

# 15. Security

Gateway áp dụng:

- HTTPS Only
- JWT Authentication
- Security Headers
- Request Validation
- Rate Limiting

Future:

- IP Whitelist
- API Key
- mTLS

---

# 16. Package Structure

```
api-gateway
│
├── config
├── filter
├── security
├── route
├── exception
├── handler
├── util
└── properties
```

---

# 17. Configuration

Ví dụ:

```yaml
server:
  port: 8080

spring:
  cloud:
    gateway:
      routes:
        - id: auth-service
          uri: lb://AUTH-SERVICE
          predicates:
            - Path=/api/v1/auth/**

        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/api/v1/users/**
```

Chi tiết cấu hình sẽ được mô tả trong tài liệu triển khai.

---

# 18. Error Codes

| Code | Description |
|------|-------------|
| GW-001 | Route Not Found |
| GW-002 | Invalid JWT |
| GW-003 | Unauthorized |
| GW-004 | Access Denied |
| GW-005 | Rate Limit Exceeded |
| GW-006 | Service Unavailable |

---

# 19. Design Decisions (ADR)

## ADR-001

### Tại sao sử dụng API Gateway?

API Gateway giúp Client chỉ cần giao tiếp với một endpoint duy nhất, giảm sự phụ thuộc vào cấu trúc bên trong của hệ thống Microservice.

---

## ADR-002

### Tại sao Gateway không chứa Business Logic?

Gateway chỉ chịu trách nhiệm xử lý các concern chung như Routing, Authentication và Logging.

Business Logic phải được đặt trong từng Microservice để đảm bảo nguyên tắc Single Responsibility.

---

## ADR-003

### Tại sao JWT được kiểm tra tại Gateway?

Việc xác thực JWT ngay tại Gateway giúp loại bỏ các request không hợp lệ trước khi chúng đến các service phía sau, giảm tải và tăng cường bảo mật.

---

## ADR-004

### Tại sao sử dụng Trace ID?

Trace ID cho phép theo dõi một request xuyên suốt nhiều Microservices, hỗ trợ việc debug, monitoring và observability.

---

# 20. Future Enhancements

- Service Discovery (Eureka/Consul)
- Circuit Breaker (Resilience4j)
- Retry Policy
- Request Caching
- Response Compression
- OpenTelemetry
- Distributed Tracing
- API Versioning
- GraphQL Gateway
- WebSocket Proxy

---

# 21. Open Questions

Các vấn đề sẽ được quyết định ở các Sprint sau.

- Có sử dụng Service Discovery ngay từ Sprint 1 hay cấu hình static route?
- Có cần Gateway kiểm tra Role hay chỉ kiểm tra JWT?
- Có triển khai Rate Limiting ngay từ MVP không?
- Có sử dụng Redis để lưu trạng thái Rate Limiting không?

---

# 22. References

- BRD
- SRS
- System Architecture
- Auth Service Design
- User Service Design
- API Contract