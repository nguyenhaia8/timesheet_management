package org.example.service.Project;

import org.example.dto.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    List<ProjectResponseDTO> findAll();
    ProjectResponseDTO findById(Integer id);
}
