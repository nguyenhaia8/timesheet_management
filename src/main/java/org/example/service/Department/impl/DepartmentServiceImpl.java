package org.example.service.Department.impl;

import org.example.dto.request.DepartmentRequestDTO;
import org.example.dto.response.DepartmentResponseDTO;
import org.example.model.Department;
import org.example.model.Employee;
import org.example.repository.DepartmentRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.Department.DepartmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public DepartmentResponseDTO save(DepartmentRequestDTO departmentRequestDTO) {
        Employee headEmployee = null;
        if (departmentRequestDTO.headEmployeeId() != null) {
            headEmployee = employeeRepository.findById(departmentRequestDTO.headEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + departmentRequestDTO.headEmployeeId()));
        }

        Department department = new Department();
        department.setName(departmentRequestDTO.name());
        department.setHeadEmployee(headEmployee);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());

        Department savedDepartment = departmentRepository.save(department);
        return toDepartmentResponseDTO(savedDepartment);
    }

    @Override
    public List<DepartmentResponseDTO> findAll() {
        return departmentRepository.findAll()
                .stream()
                .map(this::toDepartmentResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponseDTO findById(Integer id) {
        return departmentRepository.findById(id)
                .map(this::toDepartmentResponseDTO)
                .orElse(null);
    }

    @Override
    public DepartmentResponseDTO update(Integer id, DepartmentRequestDTO departmentRequestDTO) {
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        Employee headEmployee = null;
        if (departmentRequestDTO.headEmployeeId() != null) {
            headEmployee = employeeRepository.findById(departmentRequestDTO.headEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Employee not found with id: " + departmentRequestDTO.headEmployeeId()));
        }

        existingDepartment.setName(departmentRequestDTO.name());
        existingDepartment.setHeadEmployee(headEmployee);
        existingDepartment.setUpdatedAt(LocalDateTime.now());

        Department updatedDepartment = departmentRepository.save(existingDepartment);
        return toDepartmentResponseDTO(updatedDepartment);
    }

    @Override
    public void deleteById(Integer id) {
        if (!departmentRepository.existsById(id)) {
            throw new RuntimeException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentResponseDTO toDepartmentResponseDTO(Department department) {
        return new DepartmentResponseDTO(
                department.getDepartmentId(),
                department.getName(),
                department.getHeadEmployee() != null ? department.getHeadEmployee().getEmployeeId() : null,
                department.getHeadEmployee() != null ? 
                    department.getHeadEmployee().getFirstName() + " " + department.getHeadEmployee().getLastName() : null
        );
    }
}
