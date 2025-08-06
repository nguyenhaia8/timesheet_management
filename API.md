# TimeSheet Management API Documentation

This document provides a comprehensive overview of all available API endpoints in the TimeSheet Management system.

## Base URL
```
http://localhost:8080
```

## Authentication
Most endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. Authentication (`/api/auth`)

#### Login
- **POST** `/api/auth/login`
- **Description**: Authenticate user and get JWT token
- **Authentication**: Not required
- **Request Body**:
  ```json
  {
    "userName": "admin",
    "password": "password"
  }
  ```
- **Response**:
  ```json
  {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "type": "Bearer",
    "userId": 1,
    "userName": "admin",
    "roles": ["ROLE_ADMIN"]
  }
  ```

#### Signup
- **POST** `/api/auth/signup`
- **Description**: Register a new user by first creating an employee record, then creating a user account linked to that employee
- **Authentication**: Not required
- **Request Body**:
  ```json
  {
    "userName": "newuser",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@company.com",
    "position": "Software Developer",
    "departmentId": 1,
    "managerId": 2,
    "roles": ["EMPLOYEE"]
  }
  ```
- **Process**:
  1. Creates a new employee record with the provided employee information
  2. Creates a new user account linked to the newly created employee
  3. Assigns the specified roles to the user
- **Response**:
  ```json
  {
    "message": "User registered successfully!"
  }
  ```

#### Test User (Debug)
- **GET** `/api/auth/test/{userName}`
- **Description**: Test user loading (debug endpoint)
- **Authentication**: Not required
- **Response**: User information or "User not found"

#### Test Password (Debug)
- **GET** `/api/auth/test-password/{userName}`
- **Description**: Test password matching (debug endpoint)
- **Authentication**: Not required
- **Response**: Password matching information

---

### 2. TimeSheets (`/api/timesheets`)

#### Create TimeSheet
- **POST** `/api/timesheets`
- **Description**: Create a new timesheet
- **Authentication**: Required (ADMIN, MANAGER, EMPLOYEE)
- **Request Body**: `TimeSheetRequestDTO`
- **Response**: `TimeSheetResponseDTO` (201 Created)

#### Get All TimeSheets
- **GET** `/api/timesheets`
- **Description**: Get all timesheets
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `List<TimeSheetResponseDTO>` (200 OK)

#### Get TimeSheet by ID
- **GET** `/api/timesheets/{id}`
- **Description**: Get timesheet by ID
- **Authentication**: Required (ADMIN, MANAGER, or own timesheet)
- **Response**: `TimeSheetResponseDTO` (200 OK) or 404 Not Found

#### Update TimeSheet
- **PUT** `/api/timesheets/{id}`
- **Description**: Update timesheet by ID
- **Authentication**: Required (ADMIN, MANAGER, or own timesheet)
- **Request Body**: `TimeSheetRequestDTO`
- **Response**: `TimeSheetResponseDTO` (200 OK) or 404 Not Found

#### Delete TimeSheet
- **DELETE** `/api/timesheets/{id}`
- **Description**: Delete timesheet by ID
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**:
  ```json
  {
    "status": true,
    "message": "TimeSheet deleted successfully"
  }
  ```

#### Get Detailed TimeSheet by ID
- **GET** `/api/timesheets/{id}/detail`
- **Description**: Get detailed timesheet by ID, including all entries and calculated total hours
- **Authentication**: Required (ADMIN, MANAGER, or own timesheet)
- **Response**: `TimeSheetDetailResponseDTO` (200 OK) or 404 Not Found

---

#### TimeSheetDetailResponseDTO
```json
{
  "timesheetId": "integer",
  "employeeId": "integer",
  "employeeName": "string",
  "periodStartDate": "date",
  "periodEndDate": "date",
  "status": "string",
  "submissionDate": "datetime",
  "totalHours": "decimal",
  "timeSheetEntries": [
    {
      "entryId": "integer",
      "timesheetId": "integer",
      "date": "date",
      "projectId": "integer",
      "projectName": "string",
      "taskDescription": "string",
      "hoursWorked": "decimal"
    }
    // ... more entries
  ],
  "calculatedTotalHours": "decimal"
}
```

---

### 3. TimeSheet Entries (`/api/timesheet-entries`)

#### Create TimeSheet Entry
- **POST** `/api/timesheet-entries`
- **Description**: Create a new timesheet entry
- **Authentication**: Required (ADMIN, MANAGER, EMPLOYEE)
- **Request Body**: `TimeSheetEntryRequestDTO`
- **Response**: `TimeSheetEntryResponseDTO` (201 Created)

#### Get All TimeSheet Entries
- **GET** `/api/timesheet-entries`
- **Description**: Get all timesheet entries
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `List<TimeSheetEntryResponseDTO>` (200 OK)

#### Get TimeSheet Entry by ID
- **GET** `/api/timesheet-entries/{id}`
- **Description**: Get timesheet entry by ID
- **Authentication**: Required (ADMIN, MANAGER, EMPLOYEE)
- **Response**: `TimeSheetEntryResponseDTO` (200 OK) or 404 Not Found

#### Update TimeSheet Entry
- **PUT** `/api/timesheet-entries/{id}`
- **Description**: Update timesheet entry by ID
- **Authentication**: Required (ADMIN, MANAGER, EMPLOYEE)
- **Request Body**: `TimeSheetEntryRequestDTO`
- **Response**: `TimeSheetEntryResponseDTO` (200 OK) or 404 Not Found

#### Delete TimeSheet Entry
- **DELETE** `/api/timesheet-entries/{id}`
- **Description**: Delete timesheet entry by ID
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**:
  ```json
  {
    "status": true,
    "message": "TimeSheetEntry deleted successfully"
  }
  ```

---

### 4. Departments (`/api/departments`)

#### Create Department
- **POST** `/api/departments`
- **Description**: Create a new department
- **Authentication**: Required (ADMIN only)
- **Request Body**: `DepartmentRequestDTO`
- **Response**: `DepartmentResponseDTO` (201 Created)

#### Get All Departments
- **GET** `/api/departments`
- **Description**: Get all departments
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `List<DepartmentResponseDTO>` (200 OK)

#### Get Department by ID
- **GET** `/api/departments/{id}`
- **Description**: Get department by ID
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `DepartmentResponseDTO` (200 OK) or 404 Not Found

#### Update Department
- **PUT** `/api/departments/{id}`
- **Description**: Update department by ID
- **Authentication**: Required (ADMIN only)
- **Request Body**: `DepartmentRequestDTO`
- **Response**: `DepartmentResponseDTO` (200 OK) or 404 Not Found

#### Delete Department
- **DELETE** `/api/departments/{id}`
- **Description**: Delete department by ID
- **Authentication**: Required (ADMIN only)
- **Response**:
  ```json
  {
    "status": true,
    "message": "Department deleted successfully"
  }
  ```

---

### 5. Approvals (`/api/approvals`)

#### Create Approval
- **POST** `/api/approvals`
- **Description**: Create a new approval
- **Authentication**: Required (ADMIN, MANAGER)
- **Request Body**: `ApprovalRequestDTO`
- **Response**: `ApprovalResponseDTO` (201 Created)

#### Get All Approvals
- **GET** `/api/approvals`
- **Description**: Get all approvals
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `List<ApprovalResponseDTO>` (200 OK)

#### Get Approval by ID
- **GET** `/api/approvals/{id}`
- **Description**: Get approval by ID
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `ApprovalResponseDTO` (200 OK) or 404 Not Found

#### Update Approval
- **PUT** `/api/approvals/{id}`
- **Description**: Update approval by ID
- **Authentication**: Required (ADMIN, MANAGER)
- **Request Body**: `ApprovalRequestDTO`
- **Response**: `ApprovalResponseDTO` (200 OK) or 404 Not Found

#### Delete Approval
- **DELETE** `/api/approvals/{id}`
- **Description**: Delete approval by ID
- **Authentication**: Required (ADMIN only)
- **Response**:
  ```json
  {
    "status": true,
    "message": "Approval deleted successfully"
  }
  ```

---

### 6. Employees (`/api/employees`)

#### Create Employee
- **POST** `/api/employees`
- **Description**: Create a new employee
- **Authentication**: Required (ADMIN, MANAGER)
- **Request Body**: `EmployeeRequestDTO`
- **Response**: `EmployeeResponseDTO` (201 Created)

#### Get All Employees
- **GET** `/api/employees`
- **Description**: Get all employees
- **Authentication**: Required (ADMIN, MANAGER)
- **Response**: `List<EmployeeResponseDTO>` (200 OK)

#### Get Employee by ID
- **GET** `/api/employees/{id}`
- **Description**: Get employee by ID
- **Authentication**: Required (ADMIN, MANAGER, or own employee record)
- **Response**: `EmployeeResponseDTO` (200 OK) or 404 Not Found

---

### 7. Users (`/api/users`)

#### Login (Legacy)
- **GET** `/api/users/login`
- **Description**: Legacy login endpoint (use `/api/auth/login` instead)
- **Authentication**: Not required
- **Query Parameters**: `username`, `password`
- **Response**: `UserResponseDTO` (200 OK)

#### Get All Users
- **GET** `/api/users/all`
- **Description**: Get all users
- **Authentication**: Not required
- **Response**: `List<UserResponseDTO>` (200 OK)

---

### 8. Projects (`/api/projects`)

#### Get All Projects
- **GET** `/api/projects`
- **Description**: Get all projects
- **Authentication**: Not required
- **Response**: `List<ProjectResponseDTO>` (200 OK)

#### Get Project by ID
- **GET** `/api/projects/{id}`
- **Description**: Get project by ID
- **Authentication**: Not required
- **Response**: `ProjectResponseDTO` (200 OK) or 404 Not Found

---

## Data Transfer Objects (DTOs)

### Request DTOs

#### LoginRequestDTO
```json
{
  "userName": "string",
  "password": "string"
}
```

#### SignupRequestDTO
```json
{
  "userName": "string",
  "password": "string",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "position": "string",
  "departmentId": "integer",
  "managerId": "integer",
  "roles": ["string"]
}
```

#### TimeSheetRequestDTO
```json
{
  "employeeId": "integer",
  "periodStartDate": "date",
  "periodEndDate": "date"
}
```

#### TimeSheetEntryRequestDTO
```json
{
  "timesheetId": "integer",
  "date": "date",
  "projectId": "integer",
  "taskDescription": "string",
  "hoursWorked": "decimal"
}
```

#### DepartmentRequestDTO
```json
{
  "name": "string",
  "headEmployeeId": "integer",
  "description": "string"
}
```

#### ApprovalRequestDTO
```json
{
  "timesheetId": "integer",
  "approvedBy": "integer",
  "status": "string",
  "comments": "string"
}
```

#### EmployeeRequestDTO
```json
{
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "position": "string",
  "departmentId": "integer",
  "managerId": "integer"
}
```

### Response DTOs

#### JwtResponseDTO
```json
{
  "token": "string",
  "type": "string",
  "userId": "integer",
  "userName": "string",
  "roles": ["string"]
}
```

#### MessageResponseDTO
```json
{
  "message": "string"
}
```

#### TimeSheetResponseDTO
```json
{
  "timesheetId": "integer",
  "employeeId": "integer",
  "periodStartDate": "date",
  "periodEndDate": "date",
  "status": "string",
  "submissionDate": "datetime",
  "totalHours": "decimal"
}
```

#### TimeSheetEntryResponseDTO
```json
{
  "entryId": "integer",
  "timesheetId": "integer",
  "date": "date",
  "projectId": "integer",
  "taskDescription": "string",
  "hoursWorked": "decimal"
}
```

#### DepartmentResponseDTO
```json
{
  "departmentId": "integer",
  "name": "string",
  "headEmployeeId": "integer",
  "description": "string"
}
```

#### ApprovalResponseDTO
```json
{
  "approvalId": "integer",
  "timesheetId": "integer",
  "approvedBy": "integer",
  "approvedAt": "datetime",
  "status": "string",
  "comments": "string"
}
```

#### EmployeeResponseDTO
```json
{
  "employeeId": "integer",
  "firstName": "string",
  "lastName": "string",
  "email": "string",
  "position": "string",
  "departmentId": "integer",
  "managerId": "integer"
}
```

#### UserResponseDTO
```json
{
  "userId": "integer",
  "userName": "string",
  "employeeId": "integer",
  "isActive": "boolean",
  "lastLogin": "datetime"
}
```

#### ProjectResponseDTO
```json
{
  "projectId": "integer",
  "name": "string",
  "description": "string",
  "startDate": "date",
  "endDate": "date",
  "clientId": "integer",
  "projectManagerId": "integer",
  "status": "string"
}
```

---

## HTTP Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **400 Bad Request**: Invalid request data
- **401 Unauthorized**: Authentication required
- **403 Forbidden**: Insufficient permissions
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

---

## Role-Based Access Control

### Available Roles
- **ADMIN**: Full access to all endpoints
- **MANAGER**: Access to employee and timesheet management
- **EMPLOYEE**: Limited access to own data
- **HR**: Human resources specific access

### Role Hierarchy
```
ADMIN > MANAGER > EMPLOYEE
```

### Common Authorization Patterns
- `@PreAuthorize("hasRole('ADMIN')")` - Admin only
- `@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")` - Admin or Manager
- `@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")` - All authenticated users
- `@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.employee.employeeId")` - Admin or own data

---

## Error Responses

### Standard Error Format
```json
{
  "status": false,
  "message": "Error description"
}
```

### Common Error Messages
- "Authentication failed: Bad credentials"
- "User not found"
- "Employee not found"
- "Department not found"
- "TimeSheet not found"
- "TimeSheetEntry not found"
- "Approval not found"
- "Project not found"

---

## Testing the API

### 1. Login to Get JWT Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin",
    "password": "password"
  }'
```

### 2. Use Token for Protected Endpoints
```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 3. Test with Postman
1. Import the collection
2. Set the base URL to `http://localhost:8080`
3. Use the login endpoint to get a token
4. Set the Authorization header for subsequent requests

---

## Notes

- All endpoints support CORS for cross-origin requests
- JWT tokens expire after 24 hours
- Passwords are encrypted using BCrypt
- The system uses stateless authentication
- Debug endpoints are available for troubleshooting
- All timestamps are in ISO 8601 format
- Employee IDs must exist before creating users
- Foreign key relationships are enforced at the database level 