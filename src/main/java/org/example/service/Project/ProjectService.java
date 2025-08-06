package org.example.service.Project;

import org.example.dto.request.ProjectRequestDTO;
import org.example.dto.response.ProjectResponseDTO;

import java.util.List;

public interface ProjectService {
    ProjectResponseDTO save(ProjectRequestDTO projectRequestDTO);
    List<ProjectResponseDTO> findAll();
    ProjectResponseDTO findById(Integer id);
    ProjectResponseDTO update(Integer id, ProjectRequestDTO projectRequestDTO);
    void deleteById(Integer id);
}
