package org.example.service.Department;

import org.example.dto.request.DepartmentRequestDTO;
import org.example.dto.response.DepartmentResponseDTO;

import java.util.List;

public interface DepartmentService {
    DepartmentResponseDTO save(DepartmentRequestDTO departmentRequestDTO);
    List<DepartmentResponseDTO> findAll();
    DepartmentResponseDTO findById(Integer id);
    DepartmentResponseDTO update(Integer id, DepartmentRequestDTO departmentRequestDTO);
    void deleteById(Integer id);
}
