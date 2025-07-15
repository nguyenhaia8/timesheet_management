package org.example.service.Task.impl;

import org.example.dto.request.TaskRequestDTO;
import org.example.dto.response.TaskResponseDTO;
import org.example.model.Project;
import org.example.model.Task;
import org.example.model.User;
import org.example.repository.ProjectRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task();
        task1.setTaskId(1);
        task1.setTaskName("Task 1");
        Task task2 = new Task();
        task2.setTaskId(2);
        task2.setTaskName("Task 2");
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

        List<TaskResponseDTO> result = taskService.getAllTasks();
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).taskId());
        assertEquals("Task 1", result.get(0).taskName());
        assertEquals(2, result.get(1).taskId());
        assertEquals("Task 2", result.get(1).taskName());
    }

    @Test
    void testGetTaskById_Found() {
        Task task = new Task();
        task.setTaskId(1);
        task.setTaskName("Task 1");
        when(taskRepository.findById(1)).thenReturn(Optional.of(task));

        TaskResponseDTO result = taskService.getTaskById(1);
        assertNotNull(result);
        assertEquals(1, result.taskId());
        assertEquals("Task 1", result.taskName());
    }

    @Test
    void testGetTaskById_NotFound() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());
        TaskResponseDTO result = taskService.getTaskById(99);
        assertNull(result);
    }

    @Test
    void testCreateTask() {
        TaskRequestDTO request = new TaskRequestDTO(1, "Task 1", "Description", 2);
        Project project = new Project();
        project.setProjectId(1);
        User user = new User();
        user.setUserId(2);
        Task task = new Task(project, "Task 1", "Description", user);
        task.setTaskId(10);

        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.createTask(request);
        assertNotNull(result);
        assertEquals(10, result.taskId());
        assertEquals(1, result.projectId());
        assertEquals("Task 1", result.taskName());
        assertEquals("Description", result.description());
        assertEquals(2, result.createdBy());
    }

    @Test
    void testUpdateTask_Found() {
        TaskRequestDTO request = new TaskRequestDTO(1, "Updated Task", "Updated Desc", 2);
        Project project = new Project();
        project.setProjectId(1);
        User user = new User();
        user.setUserId(2);
        Task existingTask = new Task();
        existingTask.setTaskId(5);
        when(taskRepository.findById(5)).thenReturn(Optional.of(existingTask));
        when(projectRepository.findById(1)).thenReturn(Optional.of(project));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDTO result = taskService.updateTask(5, request);
        assertNotNull(result);
        assertEquals(5, result.taskId());
        assertEquals("Updated Task", result.taskName());
        assertEquals("Updated Desc", result.description());
        assertEquals(1, result.projectId());
        assertEquals(2, result.createdBy());
    }

    @Test
    void testUpdateTask_NotFound() {
        TaskRequestDTO request = new TaskRequestDTO(1, "Task", "Desc", 2);
        when(taskRepository.findById(99)).thenReturn(Optional.empty());
        TaskResponseDTO result = taskService.updateTask(99, request);
        assertNull(result);
    }

    @Test
    void testDeleteTask_Found() {
        Task task = new Task();
        task.setTaskId(7);
        when(taskRepository.findById(7)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(7);
        boolean result = taskService.deleteTask(7);
        assertTrue(result);
        verify(taskRepository, times(1)).deleteById(7);
    }

    @Test
    void testDeleteTask_NotFound() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());
        boolean result = taskService.deleteTask(99);
        assertFalse(result);
    }
} 