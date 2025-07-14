package org.example.service.Project.impl;

import org.example.dto.response.ProjectResponseDTO;
import org.example.repository.ProjectRepository;
import org.example.service.Project.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService{
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<ProjectResponseDTO> findAll() {
        return projectRepository.findAll().stream()
            .map(p -> new ProjectResponseDTO(p.getProjectId(), p.getProjectName(), p.getDescription()))
            .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDTO findById(Integer id) {
        return projectRepository.findById(id)
            .map(p -> new ProjectResponseDTO(p.getProjectId(), p.getProjectName(), p.getDescription()))
            .orElse(null);
    }


}
