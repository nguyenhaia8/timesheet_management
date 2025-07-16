# [TIMESHEET MANAGEMENT]

**Course:** Modern Programming Practices  
**Block:** July 2025  
**Instructor:** Dr. Bright Gee Varghese R  

**Team Members:**  
- [Thanh Hai Nguyen] - [619562]  
- [Adisalem Hadush Shiferaw] - [Student ID]  

**Date of Submission:** [07/17/2025]  

---

## 1. Problem Description

Provide a clear and concise explanation of the real-world problem your project aims to solve.  
Include background, motivation, and significance of the problem.

---

## 2. User Stories

Describe the system from the user's perspective using user stories:

- As an **[Employee]**, I want to **[create a task]** for a given week so that my work hours are recorded and can be approved by my manager
- As an **[Employee]**, I want to **[edit my task]** edit my timesheet before it is approved so that I can correct any mistakes.
- As a **[Manager]**, I want to **[approve or reject]** submitted tasks so that only valid work hours are recorded.
- As a **[Manager]**, I want to  **[assign tasks to employees]** so that work is distributed efficiently.
- As a **[Manager]**, I want to  **[view all timesheets]** submitted by my team so that I can monitor their work hours.


---

## 3. Functional Requirements

List the system's essential features and functionalities:

- Employees must be able to view the list of tasks assigned to them.
- Employees must be able to view the status of their submitted timesheets (pending, approved, rejected).
- Employees must be able to create and submit timesheets for a given period (e.g., weekly).
- Managers must be able to view all timesheets submitted by their team.
- Managers must be able to approve or reject submitted timesheets.
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

- **Presentation Layer:**  
  - Exposes REST APIs to clients (frontend or Postman).
  - Handles HTTP requests, input validation, and error responses.

- **Service Layer:**  
  - Implements business logic, validation, and calculations.
  - Coordinates between controllers and repositories.

- **Data Access Layer:**  
  - Uses Spring Data JPA for database CRUD operations.
  - Abstracts SQL and database details from the rest of the app.

- **Database Layer:**  
  - MySQL database with tables for Employee, Department, Project, Task, Timesheet, Approval, etc.
  - Enforces relationships and data integrity.

---

## 6. Use Case Diagram(s)  
![Use Case Diagram](images/use_case.png)  
*(Replace with actual diagram)*

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

![Project Screenshot](images/class_diagram.png)

---

## 9. Sequence Diagrams  

### Feature: Create a new Task  

```mermaid
sequenceDiagram
    participant Controller as TaskController
    participant Service as TaskServiceImpl
    participant ProjectRepo as ProjectRepository
    participant UserRepo as UserRepository
    participant TaskRepo as TaskRepository
    participant Task as Task

    Controller->>Service: createTask(taskRequestDTO)
    Service->>ProjectRepo: findById(taskRequestDTO.projectId)
    ProjectRepo-->>Service: Project
    Service->>UserRepo: findById(taskRequestDTO.createdBy)
    UserRepo-->>Service: User
    Service->>Task: new Task(Project, taskName, description, User)
    Service->>TaskRepo: save(Task)
    TaskRepo-->>Service: saved Task
    Service->>Service: toTaskResponseDTO(saved Task)
    Service-->>Controller: TaskResponseDTO
```

---

## 10. Screenshots  

- **Login Page**  
  ![Login Screenshot](images/login.png)

- **Timesheet Submission Form**  
  ![Timesheet Form](images/submit_form.png)

- **Admin Dashboard**  
  ![Admin Dashboard](images/admin_dashboard.png)

---

## 11. Installation & Deployment  

### Step 1: Clone the Repository  
```bash
git clone https://github.com/your-username/timesheet-management.git
cd timesheet-management


```

