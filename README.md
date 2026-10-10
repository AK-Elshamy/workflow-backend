# WorkFlow — Task & Project Management REST API

A Spring Boot REST API for managing projects, tasks, users, and comments, with JWT-based authentication, role-based access control, and automated unit testing.

## Overview

WorkFlow is a backend application designed to organize project collaboration and task management. It provides RESTful endpoints for managing projects and tasks while enforcing access control to protect project resources and user data.

The project focuses on backend fundamentals, layered architecture, database persistence, security, and automated testing.

## Key Features

- **Authentication & Security:** JWT-based authentication and Spring Security integration.
- **Role-Based Access Control:** Support for `USER` and `ADMIN` roles.
- **User Management:** User registration and authentication.
- **Project Management:** Create, retrieve, update, and delete projects.
- **Task Management:** Manage tasks, statuses, priorities, and assignments.
- **Project Membership:** Associate users with projects and control resource access.
- **Comment Management:** Create, retrieve, update, and delete task comments.
- **Authorization:** Restrict project access and comment operations based on ownership, membership, and user roles.
- **Input Validation:** Validate incoming request data using Jakarta Bean Validation.
- **Automated Testing:** Unit tests using JUnit and Mockito, with JaCoCo for code coverage reporting.

## Tech Stack

| Category | Technologies |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.1.1 |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8 |
| Validation | Jakarta Bean Validation |
| Testing | JUnit, Mockito, Spring Boot Test |
| Code Coverage | JaCoCo |
| Build Tool | Maven |
| Version Control | Git, GitHub |

## Architecture

The application follows a layered architecture that separates HTTP request handling, business logic, data access, and persistence.

```text
Client
  |
  v
REST Controllers
  |
  v
DTOs & Validation
  |
  v
Service Layer
  |
  v
Repository Layer
  |
  v
MySQL Database
```

- **Controllers:** Handle HTTP requests and responses.
- **DTOs:** Define request and response structures.
- **Services:** Implement business rules, resource management, and authorization checks.
- **Repositories:** Handle database access through Spring Data JPA.
- **Entities:** Represent persistent domain objects.
- **Security Layer:** Handles authentication and protects API resources.

## Data Model

The application includes the following core entities:

- **User:** Account information and user role.
- **Project:** Project details, ownership, and membership.
- **Task:** Task details, status, priority, project association, and assignee.
- **ProjectMember:** Represents project membership.
- **Comment:** Task comments associated with their authors.

## API Overview

The API is organized around RESTful resources.

| Resource | Base Endpoint | Purpose |
|---|---|---|
| Authentication | `/api/auth` | Registration and login |
| Users | `/api/users` | User-related operations |
| Projects | `/api/projects` | Project management |
| Tasks | `/api/tasks` | Task management |
| Task Comments | `/api/tasks/{id}/comments` | Create and retrieve task comments |
| Comments | `/api/comments/{id}` | Update and delete comments |

**Comment endpoints**

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/tasks/{id}/comments` | Add a comment to a task |
| `GET` | `/api/tasks/{id}/comments` | Retrieve task comments |
| `PATCH` | `/api/comments/{id}` | Update a comment |
| `DELETE` | `/api/comments/{id}` | Delete a comment |

Protected endpoints require valid authentication and applicable permissions.

## Getting Started

### Prerequisites

- JDK 25
- Maven
- MySQL 8
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/AK-Elshamy/workflow-backend.git
cd workflow-backend
```

### 2. Configure the Database

Create a MySQL database:

```sql
CREATE DATABASE workflow_db;
```

Configure the database connection in the application's configuration file. Set your database URL, username, password, and other required environment-specific settings.

Do not commit database passwords or JWT secrets to version control.

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start using the configuration defined in the project.

## Testing

The project includes unit tests for the `CommentService`, covering successful operations, missing resources, authorization failures, and administrator permissions.

Run the test suite:

```bash
mvn clean test
```

### Code Coverage

JaCoCo is used to generate a code coverage report.

```bash
mvn clean test
```

After the build completes, open:

```text
target/site/jacoco/index.html
```

The current JaCoCo report shows **100% instruction, branch, and line coverage for `CommentService`**. This metric applies to that class only, not the entire application.

## Engineering Concepts Practiced

- Layered backend architecture and separation of concerns.
- REST API design and DTO-based data transfer.
- Dependency injection and service-repository interaction.
- Authentication and authorization with Spring Security and JWT.
- Entity relationships and relational database persistence.
- Input validation and exception handling.
- Unit testing with JUnit and Mockito.
- Test coverage analysis with JaCoCo.
- Git-based development workflow.

## Future Improvements

- Integration tests for controllers and database operations.
- OpenAPI/Swagger API documentation.
- Docker-based development and deployment.
- CI pipeline for automated testing.
- Production-oriented configuration and logging.

## Author

**Ahmed Khaled Elsayed Elshamy**

Computer Science Student | Java Backend Development

GitHub: [AK-Elshamy](https://github.com/AK-Elshamy)

Project Repository: [workflow-backend](https://github.com/AK-Elshamy/workflow-backend)