package org.example.service;

import org.example.dto.request.EmployeeRequestDTO;
import org.example.dto.response.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO save(EmployeeRequestDTO person);
    List<EmployeeResponseDTO> findAll();
}
