-- Insert sample data for TimeSheet Management System

-- Insert Departments
INSERT INTO Department (name, createdAt, updatedAt) VALUES
('Engineering', NOW(), NOW()),
('Marketing', NOW(), NOW()),
('Sales', NOW(), NOW()),
('Human Resources', NOW(), NOW()),
('Finance', NOW(), NOW());

-- Insert Employees (without manager and department initially)
INSERT INTO Employee (firstName, lastName, email, position, createdAt, updatedAt) VALUES
('John', 'Smith', 'john.smith@company.com', 'Software Engineer', NOW(), NOW()),
('Sarah', 'Johnson', 'sarah.johnson@company.com', 'Senior Software Engineer', NOW(), NOW()),
('Michael', 'Brown', 'michael.brown@company.com', 'Engineering Manager', NOW(), NOW()),
('Emily', 'Davis', 'emily.davis@company.com', 'Marketing Manager', NOW(), NOW()),
('David', 'Wilson', 'david.wilson@company.com', 'Sales Representative', NOW(), NOW()),
('Lisa', 'Anderson', 'lisa.anderson@company.com', 'HR Specialist', NOW(), NOW()),
('Robert', 'Taylor', 'robert.taylor@company.com', 'Financial Analyst', NOW(), NOW()),
('Jennifer', 'Martinez', 'jennifer.martinez@company.com', 'Product Manager', NOW(), NOW()),
('Christopher', 'Garcia', 'christopher.garcia@company.com', 'QA Engineer', NOW(), NOW()),
('Amanda', 'Rodriguez', 'amanda.rodriguez@company.com', 'UX Designer', NOW(), NOW());

-- Update Employees with department and manager relationships
UPDATE Employee SET departmentId = 1, managerId = 3 WHERE employeeId = 1; -- John Smith -> Engineering, managed by Michael Brown
UPDATE Employee SET departmentId = 1, managerId = 3 WHERE employeeId = 2; -- Sarah Johnson -> Engineering, managed by Michael Brown
UPDATE Employee SET departmentId = 1 WHERE employeeId = 3; -- Michael Brown -> Engineering (no manager)
UPDATE Employee SET departmentId = 2 WHERE employeeId = 4; -- Emily Davis -> Marketing (no manager)
UPDATE Employee SET departmentId = 3, managerId = 4 WHERE employeeId = 5; -- David Wilson -> Sales, managed by Emily Davis
UPDATE Employee SET departmentId = 4 WHERE employeeId = 6; -- Lisa Anderson -> HR (no manager)
UPDATE Employee SET departmentId = 5 WHERE employeeId = 7; -- Robert Taylor -> Finance (no manager)
UPDATE Employee SET departmentId = 1, managerId = 3 WHERE employeeId = 8; -- Jennifer Martinez -> Engineering, managed by Michael Brown
UPDATE Employee SET departmentId = 1, managerId = 3 WHERE employeeId = 9; -- Christopher Garcia -> Engineering, managed by Michael Brown
UPDATE Employee SET departmentId = 1, managerId = 3 WHERE employeeId = 10; -- Amanda Rodriguez -> Engineering, managed by Michael Brown

-- Update Department heads
UPDATE Department SET headEmployeeId = 3 WHERE departmentId = 1; -- Engineering head: Michael Brown
UPDATE Department SET headEmployeeId = 4 WHERE departmentId = 2; -- Marketing head: Emily Davis
UPDATE Department SET headEmployeeId = 5 WHERE departmentId = 3; -- Sales head: David Wilson
UPDATE Department SET headEmployeeId = 6 WHERE departmentId = 4; -- HR head: Lisa Anderson
UPDATE Department SET headEmployeeId = 7 WHERE departmentId = 5; -- Finance head: Robert Taylor

-- Insert Clients
INSERT INTO Client (name, email, phone, address, createdAt, updatedAt) VALUES
('TechCorp Solutions', 'contact@techcorp.com', '+1-555-0101', '123 Tech Street, Silicon Valley, CA', NOW(), NOW()),
('Global Retail Inc', 'info@globalretail.com', '+1-555-0102', '456 Commerce Ave, New York, NY', NOW(), NOW()),
('Healthcare Systems Ltd', 'support@healthcare.com', '+1-555-0103', '789 Medical Blvd, Boston, MA', NOW(), NOW()),
('EduTech Innovations', 'hello@edutech.com', '+1-555-0104', '321 Learning Lane, Austin, TX', NOW(), NOW()),
('Green Energy Co', 'info@greenenergy.com', '+1-555-0105', '654 Solar Road, Denver, CO', NOW(), NOW());

-- Insert Projects
INSERT INTO Project (name, description, startDate, endDate, clientId, projectManagerId, status, createdAt, updatedAt) VALUES
('E-commerce Platform', 'Modern e-commerce solution with payment integration', '2024-01-15', '2024-06-30', 1, 3, 'ACTIVE', NOW(), NOW()),
('Mobile App Development', 'Cross-platform mobile application for retail', '2024-02-01', '2024-08-15', 2, 8, 'ACTIVE', NOW(), NOW()),
('Hospital Management System', 'Comprehensive healthcare management platform', '2024-03-01', '2024-12-31', 3, 3, 'PLANNING', NOW(), NOW()),
('Learning Management System', 'Online education platform with video streaming', '2024-01-01', '2024-05-30', 4, 8, 'ACTIVE', NOW(), NOW()),
('Energy Monitoring Dashboard', 'Real-time energy consumption tracking system', '2024-04-01', '2024-09-30', 5, 3, 'PLANNING', NOW(), NOW());

-- Insert EmployeeProject assignments
INSERT INTO EmployeeProject (employeeId, projectId, startDate, endDate, createdAt, updatedAt) VALUES
(1, 1, '2024-01-15', '2024-06-30', NOW(), NOW()), -- John Smith -> E-commerce Platform
(2, 1, '2024-01-15', '2024-06-30', NOW(), NOW()), -- Sarah Johnson -> E-commerce Platform
(9, 1, '2024-01-15', '2024-06-30', NOW(), NOW()), -- Christopher Garcia -> E-commerce Platform
(1, 2, '2024-02-01', '2024-08-15', NOW(), NOW()), -- John Smith -> Mobile App
(10, 2, '2024-02-01', '2024-08-15', NOW(), NOW()), -- Amanda Rodriguez -> Mobile App
(2, 3, '2024-03-01', '2024-12-31', NOW(), NOW()), -- Sarah Johnson -> Hospital System
(9, 3, '2024-03-01', '2024-12-31', NOW(), NOW()), -- Christopher Garcia -> Hospital System
(1, 4, '2024-01-01', '2024-05-30', NOW(), NOW()), -- John Smith -> Learning System
(2, 4, '2024-01-01', '2024-05-30', NOW(), NOW()), -- Sarah Johnson -> Learning System
(10, 4, '2024-01-01', '2024-05-30', NOW(), NOW()); -- Amanda Rodriguez -> Learning System

-- Insert Roles
INSERT INTO Role (roleName, description, createdAt, updatedAt) VALUES
('ADMIN', 'System administrator with full access', NOW(), NOW()),
('MANAGER', 'Department or project manager', NOW(), NOW()),
('EMPLOYEE', 'Regular employee with basic access', NOW(), NOW()),
('APPROVER', 'Can approve timesheets', NOW(), NOW()),
('VIEWER', 'Read-only access to reports', NOW(), NOW());

-- Insert Users (passwords should be hashed in production)
INSERT INTO User (userName, password, employeeId, isActive, createdAt, updatedAt) VALUES
('john.smith', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 1, true, NOW(), NOW()),
('sarah.johnson', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 2, true, NOW(), NOW()),
('michael.brown', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 3, true, NOW(), NOW()),
('emily.davis', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 4, true, NOW(), NOW()),
('david.wilson', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 5, true, NOW(), NOW()),
('lisa.anderson', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 6, true, NOW(), NOW()),
('robert.taylor', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 7, true, NOW(), NOW()),
('jennifer.martinez', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 8, true, NOW(), NOW()),
('christopher.garcia', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 9, true, NOW(), NOW()),
('amanda.rodriguez', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 10, true, NOW(), NOW());

-- Insert UserRole assignments
INSERT INTO UserRole (userId, roleId, createdAt, updatedAt) VALUES
(1, 3, NOW(), NOW()), -- John Smith -> EMPLOYEE
(2, 3, NOW(), NOW()), -- Sarah Johnson -> EMPLOYEE
(3, 2, NOW(), NOW()), -- Michael Brown -> MANAGER
(3, 4, NOW(), NOW()), -- Michael Brown -> APPROVER
(4, 2, NOW(), NOW()), -- Emily Davis -> MANAGER
(5, 3, NOW(), NOW()), -- David Wilson -> EMPLOYEE
(6, 2, NOW(), NOW()), -- Lisa Anderson -> MANAGER
(7, 3, NOW(), NOW()), -- Robert Taylor -> EMPLOYEE
(8, 2, NOW(), NOW()), -- Jennifer Martinez -> MANAGER
(9, 3, NOW(), NOW()), -- Christopher Garcia -> EMPLOYEE
(10, 3, NOW(), NOW()); -- Amanda Rodriguez -> EMPLOYEE

-- Insert Timesheets
INSERT INTO Timesheet (employeeId, periodStartDate, periodEndDate, status, submissionDate, totalHours, createdAt, updatedAt) VALUES
(1, '2024-01-01', '2024-01-15', 'APPROVED', '2024-01-16 09:00:00', 80.00, NOW(), NOW()),
(1, '2024-01-16', '2024-01-31', 'SUBMITTED', '2024-02-01 10:30:00', 85.50, NOW(), NOW()),
(2, '2024-01-01', '2024-01-15', 'APPROVED', '2024-01-16 08:45:00', 82.00, NOW(), NOW()),
(2, '2024-01-16', '2024-01-31', 'DRAFT', NULL, 0.00, NOW(), NOW()),
(3, '2024-01-01', '2024-01-15', 'APPROVED', '2024-01-16 11:00:00', 90.00, NOW(), NOW()),
(8, '2024-01-01', '2024-01-15', 'APPROVED', '2024-01-16 09:15:00', 88.00, NOW(), NOW()),
(9, '2024-01-01', '2024-01-15', 'SUBMITTED', '2024-01-16 10:00:00', 76.50, NOW(), NOW()),
(10, '2024-01-01', '2024-01-15', 'DRAFT', NULL, 0.00, NOW(), NOW());

-- Insert TimesheetEntries
INSERT INTO TimesheetEntry (timesheetId, date, projectId, taskDescription, hoursWorked, createdAt, updatedAt) VALUES
-- John Smith's approved timesheet entries
(1, '2024-01-01', 1, 'Database schema design and implementation', 8.00, NOW(), NOW()),
(1, '2024-01-02', 1, 'API development for user authentication', 8.00, NOW(), NOW()),
(1, '2024-01-03', 1, 'Frontend development - user dashboard', 8.00, NOW(), NOW()),
(1, '2024-01-04', 1, 'Payment integration testing', 8.00, NOW(), NOW()),
(1, '2024-01-05', 1, 'Code review and bug fixes', 8.00, NOW(), NOW()),
(1, '2024-01-08', 1, 'Performance optimization', 8.00, NOW(), NOW()),
(1, '2024-01-09', 1, 'Security implementation', 8.00, NOW(), NOW()),
(1, '2024-01-10', 1, 'Documentation writing', 8.00, NOW(), NOW()),
(1, '2024-01-11', 1, 'Unit testing', 8.00, NOW(), NOW()),
(1, '2024-01-12', 1, 'Integration testing', 8.00, NOW(), NOW()),

-- John Smith's submitted timesheet entries
(2, '2024-01-16', 1, 'Database optimization', 8.50, NOW(), NOW()),
(2, '2024-01-17', 1, 'API endpoint development', 8.50, NOW(), NOW()),
(2, '2024-01-18', 1, 'Frontend component development', 8.50, NOW(), NOW()),
(2, '2024-01-19', 1, 'Testing and debugging', 8.50, NOW(), NOW()),
(2, '2024-01-22', 1, 'Code review', 8.50, NOW(), NOW()),
(2, '2024-01-23', 1, 'Performance testing', 8.50, NOW(), NOW()),
(2, '2024-01-24', 1, 'Security audit', 8.50, NOW(), NOW()),
(2, '2024-01-25', 1, 'Documentation updates', 8.50, NOW(), NOW()),
(2, '2024-01-26', 1, 'Final testing', 8.50, NOW(), NOW()),
(2, '2024-01-29', 1, 'Deployment preparation', 8.50, NOW(), NOW()),

-- Sarah Johnson's approved timesheet entries
(3, '2024-01-01', 1, 'Backend API development', 8.00, NOW(), NOW()),
(3, '2024-01-02', 1, 'Database query optimization', 8.00, NOW(), NOW()),
(3, '2024-01-03', 1, 'Microservices architecture design', 8.00, NOW(), NOW()),
(3, '2024-01-04', 1, 'API documentation', 8.00, NOW(), NOW()),
(3, '2024-01-05', 1, 'Code review and mentoring', 8.00, NOW(), NOW()),
(3, '2024-01-08', 1, 'Performance monitoring setup', 8.00, NOW(), NOW()),
(3, '2024-01-09', 1, 'Security implementation', 8.00, NOW(), NOW()),
(3, '2024-01-10', 1, 'Testing framework setup', 8.00, NOW(), NOW()),
(3, '2024-01-11', 1, 'Unit test development', 8.00, NOW(), NOW()),
(3, '2024-01-12', 1, 'Integration test development', 8.00, NOW(), NOW()),

-- Jennifer Martinez's approved timesheet entries
(6, '2024-01-01', 2, 'Project planning and requirements gathering', 8.00, NOW(), NOW()),
(6, '2024-01-02', 2, 'Stakeholder meetings', 8.00, NOW(), NOW()),
(6, '2024-01-03', 2, 'Technical architecture design', 8.00, NOW(), NOW()),
(6, '2024-01-04', 2, 'Team coordination and task assignment', 8.00, NOW(), NOW()),
(6, '2024-01-05', 2, 'Progress review and planning', 8.00, NOW(), NOW()),
(6, '2024-01-08', 2, 'Risk assessment and mitigation', 8.00, NOW(), NOW()),
(6, '2024-01-09', 2, 'Quality assurance planning', 8.00, NOW(), NOW()),
(6, '2024-01-10', 2, 'Resource allocation', 8.00, NOW(), NOW()),
(6, '2024-01-11', 2, 'Timeline management', 8.00, NOW(), NOW()),
(6, '2024-01-12', 2, 'Stakeholder communication', 8.00, NOW(), NOW()),

-- Christopher Garcia's submitted timesheet entries
(7, '2024-01-01', 1, 'Test case development', 7.50, NOW(), NOW()),
(7, '2024-01-02', 1, 'Automated testing setup', 7.50, NOW(), NOW()),
(7, '2024-01-03', 1, 'Manual testing execution', 7.50, NOW(), NOW()),
(7, '2024-01-04', 1, 'Bug reporting and tracking', 7.50, NOW(), NOW()),
(7, '2024-01-05', 1, 'Test documentation', 7.50, NOW(), NOW()),
(7, '2024-01-08', 1, 'Performance testing', 7.50, NOW(), NOW()),
(7, '2024-01-09', 1, 'Security testing', 7.50, NOW(), NOW()),
(7, '2024-01-10', 1, 'User acceptance testing', 7.50, NOW(), NOW()),
(7, '2024-01-11', 1, 'Test result analysis', 7.50, NOW(), NOW()),
(7, '2024-01-12', 1, 'Test report preparation', 7.50, NOW(), NOW());

-- Insert Approvals
INSERT INTO Approval (timesheetId, approverId, status, comments, approvalDate, createdAt, updatedAt) VALUES
(1, 3, 'APPROVED', 'All entries look good. Approved.', '2024-01-17 10:00:00', NOW(), NOW()),
(3, 3, 'APPROVED', 'Excellent work. Approved.', '2024-01-17 09:30:00', NOW(), NOW()),
(5, 4, 'APPROVED', 'Manager approval granted.', '2024-01-17 11:15:00', NOW(), NOW()),
(6, 3, 'APPROVED', 'Project management work approved.', '2024-01-17 10:45:00', NOW(), NOW()),
(7, 3, 'PENDING', 'Under review', NULL, NOW(), NOW());

-- Update total hours for timesheets based on entries
UPDATE Timesheet SET totalHours = (
    SELECT COALESCE(SUM(hoursWorked), 0) 
    FROM TimesheetEntry 
    WHERE timesheetId = Timesheet.timesheetId
) WHERE timesheetId IN (1, 2, 3, 6, 7); 