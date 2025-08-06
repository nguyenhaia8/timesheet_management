package org.example.service.Project.impl;

import org.example.dto.request.ProjectRequestDTO;
import org.example.dto.response.ProjectResponseDTO;
import org.example.model.Project;
import org.example.model.Client;
import org.example.model.Employee;
import org.example.repository.ProjectRepository;
import org.example.repository.ClientRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.Project.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository, 
                            ClientRepository clientRepository, 
                            EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ProjectResponseDTO save(ProjectRequestDTO projectRequestDTO) {
        Client client = clientRepository.findById(projectRequestDTO.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + projectRequestDTO.clientId()));

        Employee projectManager = employeeRepository.findById(projectRequestDTO.projectManagerId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + projectRequestDTO.projectManagerId()));

        Project project = new Project(
                projectRequestDTO.name(),
                projectRequestDTO.description(),
                projectRequestDTO.startDate(),
                projectRequestDTO.endDate(),
                client,
                projectManager
        );

        if (projectRequestDTO.status() != null) {
            project.setStatus(Project.ProjectStatus.valueOf(projectRequestDTO.status().toUpperCase()));
        }

        Project savedProject = projectRepository.save(project);
        return toProjectResponseDTO(savedProject);
    }

    @Override
    public List<ProjectResponseDTO> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(this::toProjectResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDTO findById(Integer id) {
        return projectRepository.findById(id)
                .map(this::toProjectResponseDTO)
                .orElse(null);
    }

    @Override
    public ProjectResponseDTO update(Integer id, ProjectRequestDTO projectRequestDTO) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + id));

        Client client = clientRepository.findById(projectRequestDTO.clientId())
                .orElseThrow(() -> new RuntimeException("Client not found with id: " + projectRequestDTO.clientId()));

        Employee projectManager = employeeRepository.findById(projectRequestDTO.projectManagerId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + projectRequestDTO.projectManagerId()));

        existingProject.setName(projectRequestDTO.name());
        existingProject.setDescription(projectRequestDTO.description());
        existingProject.setStartDate(projectRequestDTO.startDate());
        existingProject.setEndDate(projectRequestDTO.endDate());
        existingProject.setClient(client);
        existingProject.setProjectManager(projectManager);

        if (projectRequestDTO.status() != null) {
            existingProject.setStatus(Project.ProjectStatus.valueOf(projectRequestDTO.status().toUpperCase()));
        }

        Project updatedProject = projectRepository.save(existingProject);
        return toProjectResponseDTO(updatedProject);
    }

    @Override
    public void deleteById(Integer id) {
        projectRepository.deleteById(id);
    }

    private ProjectResponseDTO toProjectResponseDTO(Project project) {
        return new ProjectResponseDTO(
                project.getProjectId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getClient() != null ? project.getClient().getClientId() : null,
                project.getClient() != null ? project.getClient().getClientName() : null,
                project.getProjectManager() != null ? project.getProjectManager().getEmployeeId() : null,
                project.getProjectManager() != null ? 
                    project.getProjectManager().getFirstName() + " " + project.getProjectManager().getLastName() : null,
                project.getStatus() != null ? project.getStatus().name() : null
        );
    }
}
