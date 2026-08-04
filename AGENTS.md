# SplitMate AI Context

## Project

SplitMate là nền tảng quản lý chi tiêu cá nhân và nhóm.

Người dùng có thể:

- tạo group
- thêm expense
- chia tiền
- theo dõi debt
- settlement

------------------------------------

## Tech Stack

Language

- Java 21

Framework

- Spring Boot 4.1

Architecture

- Microservices

Gateway

- Spring Cloud Gateway

Authentication

- JWT

Database

- PostgreSQL

Messaging

- Kafka

Cache

- Redis

Storage

- Cloudinary

------------------------------------

## Microservices

api-gateway

auth-service

user-service

group-service

expense-service

balance-service

budget-service

notification-service

analytics-service

------------------------------------

## Coding Standards

- Constructor Injection
- Lombok
- MapStruct
- Validation
- Global Exception Handler
- Layered Architecture
- RESTful API

------------------------------------

## Architecture Principles

- One database per service
- Event-driven communication
- No shared entity between services
- DTO only
- JWT Authentication

------------------------------------

## Database

Database Design

See:

docs/05-DATABASE-DESIGN.md

Database Schema

See:

docs/06-DATABASE-SCHEMA.md

------------------------------------

## API

API Contract

See:

docs/api/

------------------------------------

## Service Design

See:

docs/services/

------------------------------------

## Security

See:

docs/07-SECURITY-ARCHITECTURE.md

------------------------------------

## Important Rules

Never access another service database.

Never return Entity directly.

Always use DTO.

Always publish event after creating Account.

Always validate request using Jakarta Validation.
