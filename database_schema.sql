-- Create database
CREATE DATABASE IF NOT EXISTS timesheet_management;
USE timesheet_management;

-- Create Department table
CREATE TABLE Department (
    departmentId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    headEmployeeId INT,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

-- Create Employee table
CREATE TABLE Employee (
    employeeId INT AUTO_INCREMENT PRIMARY KEY,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    position VARCHAR(255) NOT NULL,
    departmentId INT,
    managerId INT,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (departmentId) REFERENCES Department(departmentId),
    FOREIGN KEY (managerId) REFERENCES Employee(employeeId)
);

-- Add foreign key for Department.headEmployeeId after Employee table is created
ALTER TABLE Department
ADD FOREIGN KEY (headEmployeeId) REFERENCES Employee(employeeId);

-- Create Client table
CREATE TABLE Client (
    clientId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address TEXT,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

-- Create Project table
CREATE TABLE Project (
    projectId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    startDate DATE NOT NULL,
    endDate DATE,
    clientId INT NOT NULL,
    projectManagerId INT NOT NULL,
    status ENUM('PLANNING', 'ACTIVE', 'COMPLETED', 'CANCELLED') DEFAULT 'PLANNING',
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (clientId) REFERENCES Client(clientId),
    FOREIGN KEY (projectManagerId) REFERENCES Employee(employeeId)
);

-- Create EmployeeProject (junction table for many-to-many relationship)
CREATE TABLE EmployeeProject (
    employeeProjectId INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT NOT NULL,
    projectId INT NOT NULL,
    startDate DATE NOT NULL,
    endDate DATE,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (employeeId) REFERENCES Employee(employeeId),
    FOREIGN KEY (projectId) REFERENCES Project(projectId)
);

-- Create Role table
CREATE TABLE Role (
    roleId INT AUTO_INCREMENT PRIMARY KEY,
    roleName VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions TEXT,
    assignedDate DATETIME,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL
);

-- Create User table
CREATE TABLE User (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    userName VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    employeeId INT,
    isActive BOOLEAN DEFAULT true,
    lastLogin DATETIME,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (employeeId) REFERENCES Employee(employeeId)
);

-- Create UserRole (junction table for many-to-many relationship)
CREATE TABLE UserRole (
    userRoleId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    roleId INT NOT NULL,
    assignedDate DATETIME,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (userId) REFERENCES User(userId),
    FOREIGN KEY (roleId) REFERENCES Role(roleId)
);

-- Create Timesheet table
CREATE TABLE Timesheet (
    timesheetId INT AUTO_INCREMENT PRIMARY KEY,
    employeeId INT NOT NULL,
    periodStartDate DATE NOT NULL,
    periodEndDate DATE NOT NULL,
    status ENUM('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED') DEFAULT 'DRAFT',
    submissionDate DATETIME,
    totalHours DECIMAL(5,2) DEFAULT 0.00,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (employeeId) REFERENCES Employee(employeeId)
);

-- Create TimesheetEntry table
CREATE TABLE TimesheetEntry (
    entryId INT AUTO_INCREMENT PRIMARY KEY,
    timesheetId INT NOT NULL,
    date DATE NOT NULL,
    projectId INT NOT NULL,
    taskDescription TEXT NOT NULL,
    hoursWorked DECIMAL(4,2) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (timesheetId) REFERENCES Timesheet(timesheetId),
    FOREIGN KEY (projectId) REFERENCES Project(projectId)
);

-- Create Approval table
CREATE TABLE Approval (
    approvalId INT AUTO_INCREMENT PRIMARY KEY,
    timesheetId INT NOT NULL,
    approverId INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',
    comments TEXT,
    approvalDate DATETIME,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,
    FOREIGN KEY (timesheetId) REFERENCES Timesheet(timesheetId),
    FOREIGN KEY (approverId) REFERENCES Employee(employeeId)
);

-- Create indexes for better performance
CREATE INDEX idx_employee_email ON Employee(email);
CREATE INDEX idx_employee_department ON Employee(departmentId);
CREATE INDEX idx_project_client ON Project(clientId);
CREATE INDEX idx_project_manager ON Project(projectManagerId);
CREATE INDEX idx_timesheet_employee ON Timesheet(employeeId);
CREATE INDEX idx_timesheet_status ON Timesheet(status);
CREATE INDEX idx_timesheet_entry_timesheet ON TimesheetEntry(timesheetId);
CREATE INDEX idx_timesheet_entry_project ON TimesheetEntry(projectId);
CREATE INDEX idx_timesheet_entry_date ON TimesheetEntry(date);
CREATE INDEX idx_approval_timesheet ON Approval(timesheetId);
CREATE INDEX idx_approval_approver ON Approval(approverId);
CREATE INDEX idx_user_employee ON User(employeeId); 