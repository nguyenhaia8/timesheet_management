# [TIMESHEET MANAGEMENT]

**Course:** CS425 - Software Engineering 
**Block:** August 2025  
**Professor:** Obinna Kalu  

**Team Members:**  
- [Thanh Hai Nguyen] - [619562]  
- [Adisalem Hadush Shiferaw] - [619567]  

**Date of Submission:** [08/14/2025]  

---

## 1. Problem Description

The Timesheet Management System is designed to address the need for efficient, accurate, and user-friendly tracking of employee work hours within a current-week-only scope. It enables employees to create tasks linked to predefined projects, log daily work hours, and submit weekly timesheets for managerial approval. Managers can view, approve, or reject submissions from their direct reports, ensuring accountability and oversight. The system enforces critical business rules such as role-based access, daily hour limits, and real-time total calculations, streamlining the approval workflow and laying a scalable foundation for future enhancements like historical tracking, advanced reporting, and cross-team functionality.

---

## 2. User Stories

Describe the system from the user's perspective using user stories:

- As an **[Employee]**, I want to **[create a task]** for a given week so that my work hours are recorded and can be approved by my manager
- As an **[Employee]**, I want to **[edit my task]** edit my timesheet before it is approved so that I can correct any mistakes.
- As a **[Manager]**, I want to **[approve or reject]** submitted tasks so that only valid work hours are recorded.
- As a **[Manager]**, I want to  **[view all timesheets]** submitted by my team so that I can monitor their work hours.



---

## 3. Functional Requirements

List the system's essential features and functionalities:

- Employees can create timesheet.
- Employees can create weekly tasks
- Employees able to view their task.
- Managers able to view all timesheets submitted by their team.
- Managers able to approve or reject submitted timesheets.
- The system must record the approval status and comments for each timesheet.
---

## 4.  Non-Functional Requirements

- **Maintainability:**
  - The codebase should follow clean code principles and be well-documented.
  - The system should be modular, allowing for easy updates and bug fixes.

- **Usability:**
  - The user interface should be intuitive and easy to navigate for all user roles.
  - The system should provide clear feedback for user actions (e.g., successful submission, errors).

---

## 5. Architecture of Project

### 5.1 Overview

Our system follows a **4-layer N-tier architecture** using modern Java Spring Boot practices:

1. **Presentation Layer (Controller)**  
   - Exposes RESTful APIs for all business entities (Employee, Department, Project, Task, Timesheet, Approval, etc.).
   - Handles HTTP requests and responses, input validation, and error handling.
   - Example: `EmployeeController`, `TaskController`, etc.

2. **Service Layer**  
   - Contains business logic and orchestrates data flow between controllers and repositories.
   - Handles validation, calculations (e.g., total work hours), and business rules.
   - Example: `EmployeeServiceImpl`, `TaskServiceImpl`, etc.

3. **Data Access Layer (Repository/DAO)**  
   - Uses Spring Data JPA repositories to abstract and manage all database operations.
   - Provides CRUD and custom query methods for each entity.
   - Example: `EmployeeRepository`, `TaskRepository`, etc.

4. **Database Layer**  
   - MySQL database stores all persistent data, including employees, timesheets, tasks, approvals, and more.
   - Enforces referential integrity and supports transactional operations.

This separation ensures modularity, testability, and maintainability.

---

### 5.2 Architecture Diagram

```mermaid
flowchart TD
    subgraph Presentation Layer
        A1[EmployeeController]
        A2[TaskController]
        A3[TimeSheetController]
        A4[ApprovalController]
    end
    subgraph Service Layer
        B1[EmployeeServiceImpl]
        B2[TaskServiceImpl]
        B3[TimeSheetServiceImpl]
        B4[ApprovalServiceImpl]
    end
    subgraph Data Access Layer
        C1[EmployeeRepository]
        C2[TaskRepository]
        C3[TimeSheetRepository]
        C4[ApprovalRepository]
    end
    subgraph Database Layer
        D1[(MySQL Database)]
    end

    A1 --> B1
    A2 --> B2
    A3 --> B3
    A4 --> B4

    B1 --> C1
    B2 --> C2
    B3 --> C3
    B4 --> C4

    C1 --> D1
    C2 --> D1
    C3 --> D1
    C4 --> D1
```

---

### 5.3 Technologies Used


- Language: [Java, JavaScript]  
- Framework: [Spring Boot, React, JUnit]  
- Database: [MySQL]  
- Tools: [Git, GitHub, Postman]  

---

### 5.4 Layer Descriptions

#### **🎯 Presentation Layer (Controllers)**
- **RESTful API Controllers**: 10 controllers handling HTTP requests and responses
  - `TimeSheetController` - Manages timesheet CRUD operations with role-based access
  - `TimeSheetEntryController` - Handles individual time entries within timesheets
  - `ApprovalController` - Manages approval workflow for timesheets
  - `EmployeeController` - Employee management operations
  - `DepartmentController` - Department CRUD operations
  - `ProjectController` - Project management functionality
  - `ClientController` - Client relationship management
  - `AuthController` - Authentication and authorization endpoints
  - `UserController` - User management operations
  - `GlobalExceptionHandler` - Centralized error handling and response formatting

- **Key Features**:
  - Role-based access control with `@PreAuthorize` annotations
  - Cross-origin resource sharing (CORS) configuration
  - Input validation and error handling
  - HTTP status code management
  - DTO-based request/response handling

#### **⚙️ Service Layer (Business Logic)**
- **Service Interfaces & Implementations**: 8 service modules with business logic
  - `TimeSheetService` - Timesheet business operations and validation
  - `TimeSheetEntryService` - Time entry calculations and rules
  - `ApprovalService` - Approval workflow logic
  - `EmployeeService` - Employee business operations
  - `DepartmentService` - Department management logic
  - `ProjectService` - Project lifecycle management
  - `ClientService` - Client relationship business logic
  - `UserService` - User authentication and management

- **Key Features**:
  - Business rule enforcement (e.g., daily hour limits, approval workflows)
  - Data transformation between DTOs and entities
  - Transaction management
  - Validation logic
  - Complex calculations (e.g., total work hours, period summaries)

#### **🗄️ Data Access Layer (Repositories)**
- **Spring Data JPA Repositories**: 11 repository interfaces for database operations
  - `TimeSheetRepository` - Timesheet CRUD with custom queries
  - `TimeSheetEntryRepository` - Time entry database operations
  - `ApprovalRepository` - Approval status tracking
  - `EmployeeRepository` - Employee data access
  - `DepartmentRepository` - Department persistence
  - `ProjectRepository` - Project data management
  - `ClientRepository` - Client data operations
  - `UserRepository` - User authentication data
  - `RoleRepository` - Role-based access control data
  - `UserRoleRepository` - User-role relationship management
  - `EmployeeProjectRepository` - Employee-project assignments

- **Key Features**:
  - Custom query methods for complex business requirements
  - Relationship management (e.g., employee-project assignments)
  - Spring Data JPA automatic query generation
  - Transactional data access

#### **🔐 Security Layer**
- **JWT Authentication & Authorization**:
  - `JwtTokenProvider` - JWT token generation and validation
  - `JwtAuthenticationFilter` - Request authentication filtering
  - `JwtAuthenticationEntryPoint` - Authentication error handling
  - `SecurityConfig` - Spring Security configuration
  - `CorsConfig` - Cross-origin request handling

#### **📊 Data Transfer Objects (DTOs)**
- **Request DTOs**: 9 DTOs for API input validation
  - `TimeSheetRequestDTO`, `TimeSheetWithEntriesRequestDTO`
  - `ApprovalRequestDTO`, `EmployeeRequestDTO`
  - `DepartmentRequestDTO`, `ProjectRequestDTO`
  - `ClientRequestDTO`, `UserRequestDTO`
  - `LoginRequestDTO`, `SignupRequestDTO`

- **Response DTOs**: 11 DTOs for API output formatting
  - `TimeSheetResponseDTO`, `TimeSheetDetailResponseDTO`, `TimeSheetEntryResponseDTO`
  - `ApprovalResponseDTO`, `EmployeeResponseDTO`
  - `DepartmentResponseDTO`, `ProjectResponseDTO`
  - `ClientResponseDTO`, `UserResponseDTO`
  - `JwtResponseDTO`, `MessageResponseDTO`

#### **🏗️ Configuration Layer**
- **Application Configuration**:
  - `DataInitializer` - Database seeding and initialization
  - `SecurityConfig` - Security and authentication setup
  - `CorsConfig` - Cross-origin resource sharing configuration

#### **🗄️ Database Layer**
- **MySQL Database**: 12 entity tables with relationships
  - Core entities: `Employee`, `Department`, `Project`, `Client`
  - Time tracking: `TimeSheet`, `TimeSheetEntry`
  - Workflow: `Approval`, `Task`
  - Authentication: `User`, `Role`, `UserRole`
  - Relationships: `EmployeeProject`
  - Audit fields: `createdAt`, `updatedAt` timestamps

- **Key Features**:
  - Referential integrity with foreign key constraints
  - Audit trail with automatic timestamp management
  - Enum-based status tracking (e.g., `TimeSheetStatus`, `ProjectStatus`)
  - Optimized indexes for performance

---

## 6. Use Case Diagram(s)  
![Use Case Diagram](images/usecase_diagram.png)

---

## 7. Use Case Descriptions  

### Use Case Name: Submit Timesheet  
- **Primary Actor(s)**: Employee  
- **Preconditions**: User must be logged in and assigned to a project  
- **Postconditions**: A timesheet is recorded in the system  
- **Main Success Scenario**:  
  1. Employee logs in  
  2. Navigates to “Submit Timesheet”  
  3. Inputs working hours for each day  
  4. Submits the form  
  5. System stores the entry and displays confirmation  

---

## 8. Class Diagram


