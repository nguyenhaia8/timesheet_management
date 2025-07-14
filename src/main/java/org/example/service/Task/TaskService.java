package org.example.service.Task;

import java.util.List;

import org.example.dto.request.TaskRequestDTO;
import org.example.dto.response.TaskResponseDTO;

public interface TaskService {
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO getTaskById(Integer id);
    TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO);
    TaskResponseDTO updateTask(Integer id, TaskRequestDTO taskRequestDTO);
    boolean deleteTask(Integer id);
}
