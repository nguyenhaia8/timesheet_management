package service;

import dto.request.EmployeeRequestDTO;
import dto.response.EmployeeResponseDTO;

import java.util.List;

public interface EmployeeService {
    void save(EmployeeRequestDTO person);
    List<EmployeeResponseDTO> findAll();
}
