package org.example.service.Task.impl;

import org.example.dto.request.TaskRequestDTO;
import org.example.dto.response.TaskResponseDTO;
import org.example.model.Task;
import org.example.model.Project;
import org.example.model.User;
import org.example.repository.TaskRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.UserRepository;
import org.example.service.Task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
            .map(this::toTaskResponseDTO)
            .collect(Collectors.toList());
    }

    @Override
    public TaskResponseDTO getTaskById(Integer id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(this::toTaskResponseDTO).orElse(null);
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        Project projectId = projectRepository.findById(taskRequestDTO.projectId()).orElse(null);
        User createdBy = null;
        if (taskRequestDTO.createdBy() != null) {
            createdBy = userRepository.findById(taskRequestDTO.createdBy()).orElse(null);
        }
        
        Task task = new Task(projectId, taskRequestDTO.taskName(), taskRequestDTO.description(), createdBy);
        Task savedTask = taskRepository.save(task);
        return toTaskResponseDTO(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Integer id, TaskRequestDTO taskRequestDTO) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isEmpty()) {
            return null;
        }
        
        Task task = existingTask.get();
        Project project = projectRepository.findById(taskRequestDTO.projectId()).orElse(null);
        User createdBy = null;
        if (taskRequestDTO.createdBy() != null) {
            createdBy = userRepository.findById(taskRequestDTO.createdBy()).orElse(null);
        }
        
        task.setProject(project);
        task.setTaskName(taskRequestDTO.taskName());
        task.setDescription(taskRequestDTO.description());
        task.setCreatedBy(createdBy);
        
        Task updatedTask = taskRepository.save(task);
        return toTaskResponseDTO(updatedTask);
    }

    @Override
    public boolean deleteTask(Integer id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

        private TaskResponseDTO toTaskResponseDTO(Task task) {
            return new TaskResponseDTO(
                task.getTaskId(),
                task.getProject() != null ? task.getProject().getProjectId() : null,
                task.getTaskName(),
                task.getDescription(),
                task.getCreatedBy() != null ? task.getCreatedBy().getUserId() : null
            );
        }
    }
