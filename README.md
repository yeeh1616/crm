# CRM System - Complete Implementation

A comprehensive Customer Relationship Management (CRM) system with full-stack implementation.

## Project Structure

```
crm/
├── crmbackend/          # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/     # Java source code
│   │   │   └── resources/ # Configuration and Liquibase migrations
│   │   └── test/         # Test files
│   └── pom.xml
└── crmfrontend/          # React frontend
    ├── src/
    └── package.json
```

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Node.js 16+
- Docker (optional, for MySQL and RabbitMQ)

### Backend Setup

1. Start MySQL and RabbitMQ:
```bash
cd crmbackend
docker-compose up -d database rabbitmq
```

2. Build and run:
```bash
cd crmbackend
mvn clean install
mvn spring-boot:run
```

Backend will be available at: `http://localhost:8080`

### Frontend Setup

1. Install dependencies:
```bash
cd crmfrontend
npm install
```

2. Start development server:
```bash
npm start
```

Frontend will be available at: `http://localhost:3000`

## Default Credentials

- **Admin**: `admin` / `admin123`
- **Users**: `user1` to `user10` / `admin123`

## Key Features

### Backend Features

✅ **Customer Management**
- CRUD operations with pagination, filtering, sorting
- Soft delete for users, permanent delete for admins
- Tag support and custom fields

✅ **Lead Management**
- Track leads through stages (NEW → CONTACTED → QUALIFIED → PROPOSAL → WON/LOST)
- Convert leads to customers
- Source tracking

✅ **Activity Logging**
- Record calls, emails, meetings, notes
- Follow-up reminders
- Link to customers or leads

✅ **Analytics & Reporting**
- Conversion rates by source
- Source analysis
- Time series data

✅ **Async Export**
- PDF/CSV/XLSX export via RabbitMQ
- WebSocket real-time status updates
- Spring Cache for performance

✅ **Security**
- JWT authentication
- Role-based access control (user/admin)
- Idempotency support

✅ **Database**
- Liquibase migrations
- Initial seed data (10 users, 100 customers, 100 activities, 50 leads)

### Frontend Features

✅ **Authentication**
- Login/Logout
- JWT token management

✅ **Data Management**
- View customers, leads, activities
- Tabbed interface

✅ **Export**
- Export buttons (CSV/XLSX/PDF)
- Real-time status via WebSocket
- Download links

✅ **Analytics**
- Dashboard view

## API Documentation

### Swagger/OpenAPI

Complete API documentation is available via Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Features

- All endpoints fully documented with request/response schemas
- Interactive API testing directly from the browser
- JWT authentication support in Swagger UI
- Parameter descriptions and examples
- Error response documentation

### Using Swagger UI

1. Start the backend application
2. Navigate to http://localhost:8080/swagger-ui.html
3. Use `/api/auth/login` to get a JWT token
4. Click "Authorize" and enter: `Bearer <your-token>`
5. Test any endpoint directly from the UI

## Architecture

### Backend Architecture

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: Separate DTOs for API communication
- **JPA Entities**: Database entities with relationships
- **Liquibase**: Database version control
- **Spring Cache**: Caffeine-based caching
- **RabbitMQ**: Message queue for async processing
- **WebSocket**: Real-time notifications

### Database Schema

- `users` - System users (user/admin roles)
- `customers` - Customer records
- `leads` - Sales leads
- `activities` - Follow-up logs
- `tags` - Customer tags
- `scores` - Activity-based scoring
- `segments` - Customer segmentation
- `idempotency_key` - Idempotency tracking

## Testing

### Backend Tests

```bash
cd crmbackend
mvn test
```

**Coverage Target**: ≥80%

Test coverage reports are generated using JaCoCo and available at:
```
crmbackend/target/site/jacoco/index.html
```

### Test Types

- **Unit Tests**: Service and controller layer tests with mocked dependencies
- **Integration Tests**: Full Spring Boot context tests with H2 in-memory database

### Running Tests

```bash
# Run all tests
cd crmbackend
mvn test

# Run with coverage report
mvn clean test

# View coverage report
# Open: crmbackend/target/site/jacoco/index.html
```

### Integration Tests

Uses Testcontainers for MySQL integration testing.

## Deployment

### Docker

```bash
cd crmbackend
docker-compose up -d
```

### Production Build

**Backend:**
```bash
cd crmbackend
mvn clean package
java -jar target/crmbackend-0.0.1-SNAPSHOT.jar
```

**Frontend:**
```bash
cd crmfrontend
npm run build
# Serve the build/ directory with a web server
```

## Configuration

### Backend Configuration

Edit `crmbackend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:8081/crmdb
    username: root
    password: root123
  rabbitmq:
    host: localhost
    port: 5672
    username: admin
    password: admin123
```

### Frontend Configuration

Edit `crmfrontend/src/App.js`:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Development Notes

- All code comments are in English
- API supports idempotency via `Idempotency-Key` header
- Soft delete implemented for customers (users can only soft delete)
- Admin users can permanently delete records
- Export tasks are processed asynchronously via RabbitMQ
- WebSocket provides real-time export status updates

## Future Enhancements

- [ ] Advanced segmentation rules engine
- [ ] Activity scoring automation
- [ ] Email notifications for follow-ups
- [ ] Advanced reporting with charts
- [ ] Multi-tenant support
- [ ] Third-party CRM integration

## License

This project is for demonstration purposes.

## Support

For issues or questions, please refer to the documentation in each module's README.

