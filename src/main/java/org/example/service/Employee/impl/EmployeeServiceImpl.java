package org.example.service.Employee.impl;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;
import org.example.model.Employee;
import org.example.model.Department;
import org.example.repository.EmployeeRepository;
import org.example.repository.DepartmentRepository;
import org.example.service.Employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public EmployeeResponseDTO save(EmployeeRequestDTO employeeRequestDTO) {
        // Fetch Department entity
        Department department = null;
        if (employeeRequestDTO.departmentId() != null) {
            department = departmentRepository.findById(employeeRequestDTO.departmentId()).orElse(null);
        }
        // Fetch Manager Employee entity
        Employee manager = null;
        if (employeeRequestDTO.managerId() != null) {
            manager = employeeRepository.findById(employeeRequestDTO.managerId()).orElse(null);
        }
        // construct employee object from EmployeeRequestDTO
        Employee employee = new Employee(
            employeeRequestDTO.firstName(),
            employeeRequestDTO.lastName(),
            employeeRequestDTO.email(),
            department,
            manager
        );

        // save to database using Spring Data JPA
        Employee savedEmployee = employeeRepository.save(employee);
        
        // convert to response DTO
        return new EmployeeResponseDTO(
            savedEmployee.getEmployeeId(),
            savedEmployee.getFirstName(),
            savedEmployee.getLastName(),
            savedEmployee.getEmail(),
            savedEmployee.getDepartment() != null ? savedEmployee.getDepartment().getDepartmentId() : null,
            savedEmployee.getDepartment() != null ? savedEmployee.getDepartment().getDepartmentName() : null,
            savedEmployee.getManager() != null ? savedEmployee.getManager().getEmployeeId() : null,
            savedEmployee.getManager() != null ? savedEmployee.getManager().getFirstName() + " " + savedEmployee.getManager().getLastName() : null
        );
    }

    @Override
    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll()
            .stream()
            .map(employee -> new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null,
                employee.getManager() != null ? employee.getManager().getEmployeeId() : null,
                employee.getManager() != null ? employee.getManager().getFirstName() + " " + employee.getManager().getLastName() : null
            ))
            .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO findById(Integer id) {
        return employeeRepository.findById(id)
            .map(employee -> new EmployeeResponseDTO(
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getDepartmentName() : null,
                employee.getManager() != null ? employee.getManager().getEmployeeId() : null,
                employee.getManager() != null ? employee.getManager().getFirstName() + " " + employee.getManager().getLastName() : null
            ))
            .orElse(null);
    }
}
