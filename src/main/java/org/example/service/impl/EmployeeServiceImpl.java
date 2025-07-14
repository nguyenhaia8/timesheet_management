package org.example.service.impl;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;
import org.example.model.Employee;
import org.example.repository.EmployeeRepository;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponseDTO save(EmployeeRequestDTO employeeRequestDTO) {
        // construct employee object from EmployeeRequestDTO
        Employee employee = new Employee(
            employeeRequestDTO.employeeCode(),
            employeeRequestDTO.firstName(),
            employeeRequestDTO.lastName(),
            employeeRequestDTO.department()
        );

        // save to database using Spring Data JPA
        Employee savedEmployee = employeeRepository.save(employee);
        
        // convert to response DTO
        return new EmployeeResponseDTO(
            savedEmployee.getId(),
            savedEmployee.getEmployeeCode(),
            savedEmployee.getFirstName(),
            savedEmployee.getLastName(),
            savedEmployee.getDepartment()
        );
    }

    @Override
    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll()
            .stream()
            .map(employee -> new EmployeeResponseDTO(
                employee.getId(),
                employee.getEmployeeCode(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getDepartment()
            ))
            .collect(Collectors.toList());
    }
}
