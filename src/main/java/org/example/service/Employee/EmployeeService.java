package org.example.service.Employee;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO save(EmployeeRequestDTO employee);
    List<EmployeeResponseDTO> findAll();
    EmployeeResponseDTO findById(Integer id);
    EmployeeResponseDTO update(Integer id, EmployeeRequestDTO employee);
    void deleteById(Integer id);
}
