# CRM Backend System

A comprehensive Customer Relationship Management (CRM) system built with Spring Boot 3.x.

## Features

- **Customer Management**: Full CRUD operations with soft delete support
- **Lead Management**: Track sales leads through various stages
- **Activity Logging**: Record follow-ups, calls, emails, and meetings
- **Analytics & Reporting**: Conversion rates, source analysis, time series data
- **Async Export**: PDF/CSV/XLSX export with RabbitMQ and WebSocket notifications
- **Authentication**: JWT-based authentication with role-based access control (user/admin)
- **Idempotency**: Support for idempotent API requests
- **Caching**: Spring Cache with Caffeine for performance optimization

## Technology Stack

- **Framework**: Spring Boot 3.5.8
- **Java**: 17+
- **Database**: MySQL 8+
- **ORM**: Spring Data JPA
- **Migration**: Liquibase
- **Message Queue**: RabbitMQ
- **WebSocket**: Spring WebSocket (STOMP)
- **Cache**: Caffeine
- **Security**: Spring Security + JWT
- **API Documentation**: OpenAPI/Swagger

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8+
- RabbitMQ (or Docker)

## Setup

### 1. Database Setup

Start MySQL using Docker Compose:

```bash
cd crmbackend
docker-compose up -d database rabbitmq
```

Or configure MySQL manually and update `application.yml` with your database credentials.

### 2. Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:8081/crmdb
    username: root
    password: root123
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Swagger/OpenAPI

The API is fully documented using OpenAPI 3.0 (Swagger). Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### API Endpoints Documentation

All endpoints are documented with:
- Request/response schemas
- Parameter descriptions
- Authentication requirements
- Example requests and responses
- Error response codes

### Authentication in Swagger

1. Use the `/api/auth/login` endpoint to obtain a JWT token
2. Click the "Authorize" button in Swagger UI
3. Enter: `Bearer <your-token>`
4. All authenticated endpoints will now work

### API Groups

- **Authentication**: Login and token management
- **Customers**: Customer CRUD operations with filtering and pagination
- **Leads**: Lead management and conversion to customers
- **Activities**: Activity logging (calls, emails, meetings)
- **Analytics**: Conversion rates and source analysis
- **Tags**: Tag management
- **Segments**: Customer segmentation
- **Exports**: PDF report generation

## Default Credentials

- **Admin**: username: `admin`, password: `admin123`
- **Users**: username: `user1` to `user10`, password: `admin123`

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login and get JWT token

### Customers
- `GET /api/customers` - List all customers (paginated)
- `GET /api/customers/{id}` - Get customer by ID
- `POST /api/customers` - Create customer
- `PUT /api/customers/{id}` - Update customer
- `DELETE /api/customers/{id}` - Delete customer (soft delete for users, permanent for admins)

### Leads
- `GET /api/leads` - List all leads
- `GET /api/leads/{id}` - Get lead by ID
- `POST /api/leads` - Create lead
- `PUT /api/leads/{id}` - Update lead
- `POST /api/leads/{id}/convert` - Convert lead to customer
- `DELETE /api/leads/{id}` - Delete lead

### Activities
- `GET /api/activities` - List all activities
- `GET /api/activities/{id}` - Get activity by ID
- `POST /api/activities` - Create activity
- `PUT /api/activities/{id}` - Update activity
- `DELETE /api/activities/{id}` - Delete activity

### Analytics
- `GET /api/analytics/conversion-rates` - Get conversion rates by source
- `GET /api/analytics/source-analysis` - Get source analysis

### Export
- `POST /api/exports` - Initiate export (returns task ID)
- `GET /api/exports/status/{taskId}` - Get export status
- `GET /api/exports/download/{fileName}` - Download exported file

## WebSocket

WebSocket endpoint for real-time export status updates:
- Connect to: `ws://localhost:8080/ws`
- Subscribe to: `/topic/export-status`

## Idempotency

All POST/PUT/PATCH requests support idempotency via `Idempotency-Key` header:

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Authorization: Bearer <token>" \
  -H "Idempotency-Key: unique-key-123" \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com",...}'
```

## Testing

### Unit Tests

Run unit tests with:

```bash
mvn test
```

### Test Coverage

The project uses JaCoCo for code coverage analysis. Coverage reports are generated automatically after running tests.

**Coverage Target**: ≥80%

View coverage report:
```bash
# After running tests, open in browser:
target/site/jacoco/index.html
```

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with coverage report
mvn clean test

# Run specific test class
mvn test -Dtest=CustomerServiceTest

# Run tests and skip integration tests
mvn test -Dtest=*Test
```

### Test Structure

- **Unit Tests**: Located in `src/test/java/com/example/crmbackend/service/` and `controller/`
  - Service layer tests with mocked dependencies
  - Controller tests with `@WebMvcTest`
  - Security and JWT service tests

- **Integration Tests**: Located in `src/test/java/com/example/crmbackend/integration/`
  - Full Spring Boot context tests
  - Database integration tests
  - End-to-end API tests

### Test Configuration

Test profile uses H2 in-memory database (configured in `application-test.yml`) to avoid requiring MySQL during test execution.

## Docker

Build and run with Docker:

```bash
docker-compose up -d
```

## License

This project is for demonstration purposes.

