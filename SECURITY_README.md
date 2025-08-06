# Spring Security Implementation Guide

This document explains how Spring Security has been implemented in the TimeSheet Management project with the complete updated database schema.

## Overview

The project now includes a complete Spring Security implementation with:
- JWT-based authentication
- Role-based authorization
- Password encryption
- Stateless session management
- Complete database schema support

## Complete Database Schema

### 1. **Department Table**:
   - `departmentId` (Primary Key)
   - `name` (VARCHAR, unique)
   - `headEmployeeId` (Foreign Key to Employee)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 2. **Employee Table**:
   - `employeeId` (Primary Key)
   - `firstName` (VARCHAR)
   - `lastName` (VARCHAR)
   - `email` (VARCHAR, unique)
   - `position` (VARCHAR)
   - `departmentId` (Foreign Key to Department)
   - `managerId` (Foreign Key to Employee)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 3. **Client Table**:
   - `clientId` (Primary Key)
   - `clientName` (VARCHAR, unique)
   - `contactEmail` (VARCHAR)
   - `contactPhone` (VARCHAR)
   - `address` (TEXT)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 4. **Project Table**:
   - `projectId` (Primary Key)
   - `name` (VARCHAR)
   - `description` (TEXT)
   - `startDate` (DATE)
   - `endDate` (DATE)
   - `clientId` (Foreign Key to Client)
   - `projectManagerId` (Foreign Key to Employee)
   - `status` (ENUM: Planning, Active, Completed, Cancelled)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 5. **EmployeeProject Table** (Junction):
   - `employeeProjectId` (Primary Key)
   - `employeeId` (Foreign Key to Employee)
   - `projectId` (Foreign Key to Project)
   - `assignedDate` (DATE)
   - `roleInProject` (VARCHAR)
   - `isActive` (BOOLEAN)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 6. **User Table**:
   - `userId` (Primary Key)
   - `userName` (VARCHAR, unique)
   - `password` (VARCHAR, for hashed passwords)
   - `employeeId` (Foreign Key to Employee)
   - `isActive` (BOOLEAN)
   - `lastLogin` (TIMESTAMP)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 7. **Role Table**:
   - `roleId` (Primary Key)
   - `roleName` (VARCHAR, unique)
   - `description` (TEXT)
   - `permissions` (JSON)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 8. **UserRole Table** (Junction):
   - `userRoleId` (Primary Key)
   - `userId` (Foreign Key to User)
   - `roleId` (Foreign Key to Role)
   - `assignedDate` (DATE)
   - `createdAt` (TIMESTAMP)

### 9. **Timesheet Table**:
   - `timesheetId` (Primary Key)
   - `employeeId` (Foreign Key to Employee)
   - `periodStartDate` (DATE)
   - `periodEndDate` (DATE)
   - `status` (ENUM: Draft, Submitted, Approved, Rejected)
   - `submissionDate` (TIMESTAMP)
   - `totalHours` (DECIMAL)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 10. **TimesheetEntry Table**:
   - `entryId` (Primary Key)
   - `timesheetId` (Foreign Key to Timesheet)
   - `date` (DATE)
   - `projectId` (Foreign Key to Project)
   - `taskDescription` (TEXT)
   - `hoursWorked` (DECIMAL)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

### 11. **Approval Table**:
   - `approvalId` (Primary Key)
   - `timesheetId` (Foreign Key to Timesheet)
   - `approvedBy` (Foreign Key to Employee)
   - `approvedAt` (TIMESTAMP)
   - `status` (ENUM: Pending, Approved, Rejected)
   - `comments` (TEXT)
   - `createdAt` (TIMESTAMP)
   - `updatedAt` (TIMESTAMP)

## Components Added

### 1. Dependencies (pom.xml)
- `spring-boot-starter-security`: Core Spring Security
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson`: JWT token handling

### 2. Security Configuration
- **SecurityConfig.java**: Main security configuration
- **JwtAuthenticationEntryPoint.java**: Handles unauthorized requests
- **JwtAuthenticationFilter.java**: Processes JWT tokens in requests
- **JwtTokenProvider.java**: Generates and validates JWT tokens

### 3. Data Models
- **Role.java**: Role entity matching database schema
- **UserRole.java**: Junction entity for many-to-many relationship
- **User.java**: Updated to implement UserDetails interface
- **Employee.java**: Updated with new fields
- **Department.java**: Updated with headEmployee relationship
- **Client.java**: New entity for client management
- **Project.java**: Updated with status enum and relationships
- **EmployeeProject.java**: Junction entity for employee-project assignments
- **TimeSheet.java**: Updated with period dates and status enum
- **TimeSheetEntry.java**: Updated to reference projects instead of tasks
- **Approval.java**: Updated with proper relationships and status enum

### 4. Authentication
- **AuthController.java**: Handles login and registration
- **UserService.java**: Updated to implement UserDetailsService

### 5. DTOs
- **LoginRequestDTO.java**: Login request structure
- **SignupRequestDTO.java**: Registration request structure
- **JwtResponseDTO.java**: JWT response structure
- **MessageResponseDTO.java**: Simple message responses

## API Endpoints

### Public Endpoints (No Authentication Required)
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration

### Protected Endpoints (Authentication Required)
All other endpoints require a valid JWT token in the Authorization header.

## Usage Examples

### 1. User Registration
```bash
POST /api/auth/signup
Content-Type: application/json

{
  "userName": "john.doe",
  "password": "password123",
  "employeeId": 1,
  "roles": ["EMPLOYEE"]
}
```

### 2. User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "userName": "john.doe",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "userId": 1,
  "userName": "john.doe",
  "roles": ["ROLE_EMPLOYEE"]
}
```

### 3. Accessing Protected Endpoints
```bash
GET /api/employees
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## Role-Based Access Control

### Available Roles
- `ROLE_ADMIN`: Full access to all endpoints
- `ROLE_MANAGER`: Access to employee and timesheet management
- `ROLE_EMPLOYEE`: Limited access to own data

### Example Role Annotations
```java
@PreAuthorize("hasRole('ADMIN')")                    // Admin only
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')") // Admin or Manager
@PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.employee.employeeId") // Admin or own data
```

## Configuration

### JWT Configuration (application.properties)
```properties
app.jwtSecret=your-secret-key-here-make-it-long-and-secure-for-production
app.jwtExpirationInMs=86400000
```

### Security Configuration
- CORS enabled for cross-origin requests
- CSRF disabled for stateless API
- Session management set to STATELESS
- Password encryption using BCrypt

## Database Setup

### 1. Create Database Tables
The application automatically creates all tables using JPA with `spring.jpa.hibernate.ddl-auto=update`.

### 2. Default Roles
The application automatically creates default roles on startup:
- ADMIN
- MANAGER
- EMPLOYEE

### 3. Sample Data Setup
The application includes a comprehensive test data script (`create_test_data.sql`) that inserts:

#### Pre-configured Test Data:
- **3 Departments**: IT, HR, Finance
- **7 Employees**: Various positions across departments
- **3 Clients**: ABC Corp, XYZ Inc, DEF Ltd
- **3 Projects**: Website Redesign, Mobile App, Database Migration
- **Employee-Project Assignments**: Linking employees to projects
- **2 Users**: admin (ADMIN role) and newuser (EMPLOYEE role)
- **User-Role Assignments**: Proper role assignments
- **Timesheets**: Sample timesheet data for employees
- **Timesheet Entries**: Detailed time entries for projects
- **Approvals**: Sample approval records

#### Test Users Available:
1. **admin** / **password** (ROLE_ADMIN)
2. **newuser** / **password123** (ROLE_EMPLOYEE)

#### To Load Test Data:
```sql
-- Execute the create_test_data.sql script in your MySQL database
-- This will populate all tables with comprehensive test data
```

## Security Best Practices Implemented

1. **Password Encryption**: All passwords are encrypted using BCrypt
2. **JWT Token Expiration**: Tokens expire after 24 hours
3. **Role-Based Authorization**: Fine-grained access control
4. **Stateless Sessions**: No server-side session storage
5. **CORS Configuration**: Proper cross-origin request handling
6. **Last Login Tracking**: Updates user's last login timestamp
7. **Account Status**: Active/inactive user management

## Testing the Implementation

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Load Test Data
Execute the `create_test_data.sql` script in your MySQL database to populate all tables with comprehensive test data including users, roles, and sample business data.

### 3. Test with Existing Users
The application comes with pre-configured test users:

#### Available Test Users:
1. **Admin User**:
   - Username: `admin`
   - Password: `password`
   - Role: `ROLE_ADMIN`
   - Employee ID: 1

2. **New User**:
   - Username: `newuser`
   - Password: `password123`
   - Role: `ROLE_EMPLOYEE`
   - Employee ID: 7

#### Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "admin",
    "password": "password"
  }'
```

Or for the new user:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "newuser",
    "password": "password123"
  }'
```

#### Register Additional Users
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "userName": "manager1",
    "password": "manager123",
    "employeeId": 2,
    "roles": ["MANAGER"]
  }'
```

### 4. Use Token for Protected Endpoints
```bash
curl -X GET http://localhost:8080/api/employees \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 5. Test Debug Endpoints
The application includes debug endpoints for troubleshooting:

#### Test User Loading
```bash
curl -X GET http://localhost:8080/api/auth/test/admin
```

#### Test Password Matching
```bash
curl -X GET http://localhost:8080/api/auth/test-password/admin
```

## Troubleshooting

### Common Issues
1. **401 Unauthorized**: Check if JWT token is valid and not expired
2. **403 Forbidden**: Check if user has required role
3. **CORS Issues**: Verify CORS configuration in SecurityConfig
4. **Employee Not Found**: Ensure employee exists before creating user
5. **Database Constraints**: Ensure foreign key relationships are properly set up

### Debug Mode
Enable debug logging in application.properties:
```properties
logging.level.org.springframework.security=DEBUG
```

## Next Steps

1. **Customize Roles**: Add more specific roles based on your business requirements
2. **Password Policies**: Implement password strength requirements
3. **Account Lockout**: Add account lockout after failed login attempts
4. **Audit Logging**: Log authentication and authorization events
5. **Refresh Tokens**: Implement token refresh mechanism for better security
6. **Permission System**: Utilize the JSON permissions field in Role table for granular permissions
7. **Timesheet Workflow**: Implement complete timesheet submission and approval workflow
8. **Project Management**: Add project assignment and tracking features 

## Test User Credentials

| Username | Password | Role | Employee ID | Description |
|----------|----------|------|-------------|-------------|
| admin | password | ADMIN | 1 | System Administrator |
| newuser | password123 | EMPLOYEE | 7 | Regular Employee |
| testuser | password | ADMIN | - | Test Admin User |
| manager | password | MANAGER | - | Department Manager |
| employee1 | password | EMPLOYEE | - | Regular Employee 1 |
| employee2 | password | EMPLOYEE | - | Regular Employee 2 |
| qa_lead | password | MANAGER | - | QA Team Lead |
| designer | password | EMPLOYEE | - | UI/UX Designer |
| hr_manager | password | HR | - | HR Manager |

### Notes:
- **admin** and **newuser** are the primary test accounts with confirmed working credentials
- Other users may need to be created via the signup API or database insertion
- All passwords are BCrypt encrypted in the database
- Employee ID is only available for users that have corresponding Employee records