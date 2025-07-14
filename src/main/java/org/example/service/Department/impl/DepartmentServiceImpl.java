package org.example.service.Department.impl;

import org.example.dto.response.DepartmentResponseDTO;
import org.example.service.Department.DepartmentService;
import org.springframework.stereotype.Service;
import org.example.repository.DepartmentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
            .map(d -> new DepartmentResponseDTO(d.getDepartmentId(), d.getDepartmentName(), d.getDescription()))
            .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Integer id) {
        return departmentRepository.findById(id)
            .map(d -> new DepartmentResponseDTO(d.getDepartmentId(), d.getDepartmentName(), d.getDescription()))
            .orElse(null);
    }
}
