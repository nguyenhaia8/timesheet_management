package org.example;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;
import org.example.service.Employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

//@Component
public class EmployeeTest implements CommandLineRunner {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public void run(String... args) throws Exception {
        // Create test employee data
        EmployeeRequestDTO testEmployee = new EmployeeRequestDTO(
            "Even",
            "Jackson",
            "even.jackson@company.com",
            1, // departmentId (example)
            null // managerId (example, or use an Integer managerId)
        );
        
        System.out.println("Testing createEmployee functionality...");
        System.out.println("Employee to create: " + testEmployee);
        
        try {
            // Call the save method which internally calls createEmployee
            //EmployeeResponseDTO createdEmployee = employeeService.save(testEmployee);
            System.out.println("Employee created successfully!");
            //System.out.println("Created employee: " + createdEmployee);
            
            // Test getting all employees
            System.out.println("All employees:");
            employeeService.findAll().forEach(System.out::println);
            
        } catch (Exception e) {
            System.err.println("Error creating employee: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 