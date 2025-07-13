package service.impl;

import dto.request.EmployeeRequestDTO;
import dto.response.EmployeeResponseDTO;
import service.EmployeeService;

import java.util.List;

public class EmployeeServiceImpl implements EmployeeService {
    @Override
    public void save(EmployeeRequestDTO employee) {

    }

    @Override
    public List<EmployeeResponseDTO> findAll() {
        return List.of();
    }
}
