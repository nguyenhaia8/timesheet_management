package org.example.service.Employee.impl;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;
import org.example.model.Employee;
import org.example.model.Department;
import org.example.repository.EmployeeRepository;
import org.example.repository.DepartmentRepository;
import org.example.service.Employee.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

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
            employeeRequestDTO.position(),
            department,
            manager
        );

        // save to database using Spring Data JPA
        Employee savedEmployee = employeeRepository.save(employee);
        
        // convert to response DTO
        return toEmployeeResponseDTO(savedEmployee);
    }

    @Override
    public List<EmployeeResponseDTO> findAll() {
        return employeeRepository.findAll()
            .stream()
            .map(this::toEmployeeResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO findById(Integer id) {
        return employeeRepository.findById(id)
            .map(this::toEmployeeResponseDTO)
            .orElse(null);
    }

    @Override
    public EmployeeResponseDTO update(Integer id, EmployeeRequestDTO employeeRequestDTO) {
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

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

        existingEmployee.setFirstName(employeeRequestDTO.firstName());
        existingEmployee.setLastName(employeeRequestDTO.lastName());
        existingEmployee.setEmail(employeeRequestDTO.email());
        existingEmployee.setPosition(employeeRequestDTO.position());
        existingEmployee.setDepartment(department);
        existingEmployee.setManager(manager);

        Employee updatedEmployee = employeeRepository.save(existingEmployee);
        return toEmployeeResponseDTO(updatedEmployee);
    }

    @Override
    public void deleteById(Integer id) {
        employeeRepository.deleteById(id);
    }

    private EmployeeResponseDTO toEmployeeResponseDTO(Employee employee) {
        return new EmployeeResponseDTO(
            employee.getEmployeeId(),
            employee.getFirstName(),
            employee.getLastName(),
            employee.getEmail(),
            employee.getPosition(),
            employee.getDepartment() != null ? employee.getDepartment().getDepartmentId() : null,
            employee.getDepartment() != null ? employee.getDepartment().getName() : null,
            employee.getManager() != null ? employee.getManager().getEmployeeId() : null,
            employee.getManager() != null ? employee.getManager().getFirstName() + " " + employee.getManager().getLastName() : null
        );
    }
}
