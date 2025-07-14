package org.example.service.TimeSheetEntry.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;
import org.example.model.Task;
import org.example.model.TimeSheet;
import org.example.model.TimeSheetEntry;
import org.example.repository.TaskRepository;
import org.example.repository.TimeSheetEntryRepository;
import org.example.repository.TimeSheetRepository;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeSheetEntryServiceImpl implements TimeSheetEntryService {

    @Autowired
    private TimeSheetEntryRepository timeSheetEntryRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<TimeSheetEntryResponseDTO> getAllTimeSheetEntries() {
        List<TimeSheetEntry> timeSheetEntries = timeSheetEntryRepository.findAll();
        return timeSheetEntries.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSheetEntryResponseDTO getTimeSheetEntryById(Integer id) {
        TimeSheetEntry timeSheetEntry = timeSheetEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSheetEntry not found with id: " + id));
        return convertToResponseDTO(timeSheetEntry);
    }

    @Override
    public TimeSheetEntryResponseDTO createTimeSheetEntry(TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        // Validate required entities exist
        TimeSheet timeSheet = timeSheetRepository.findById(timeSheetEntryRequestDTO.timeSheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + timeSheetEntryRequestDTO.timeSheetId()));
        
        Task task = taskRepository.findById(timeSheetEntryRequestDTO.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + timeSheetEntryRequestDTO.taskId()));

        // Create new TimeSheetEntry
        TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
        timeSheetEntry.setTimeSheet(timeSheet);
        timeSheetEntry.setTask(task);
        timeSheetEntry.setWorkDate(timeSheetEntryRequestDTO.workDate());
        timeSheetEntry.setHoursWorked(BigDecimal.valueOf(timeSheetEntryRequestDTO.hoursWorked()));
        timeSheetEntry.setDescription(timeSheetEntryRequestDTO.description());

        TimeSheetEntry savedEntry = timeSheetEntryRepository.save(timeSheetEntry);
        return convertToResponseDTO(savedEntry);
    }

    @Override
    public TimeSheetEntryResponseDTO updateTimeSheetEntry(Integer id, TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        TimeSheetEntry existingEntry = timeSheetEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSheetEntry not found with id: " + id));

        // Validate required entities exist
        TimeSheet timeSheet = timeSheetRepository.findById(timeSheetEntryRequestDTO.timeSheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + timeSheetEntryRequestDTO.timeSheetId()));
        
        Task task = taskRepository.findById(timeSheetEntryRequestDTO.taskId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + timeSheetEntryRequestDTO.taskId()));

        // Update fields
        existingEntry.setTimeSheet(timeSheet);
        existingEntry.setTask(task);
        existingEntry.setWorkDate(timeSheetEntryRequestDTO.workDate());
        existingEntry.setHoursWorked(BigDecimal.valueOf(timeSheetEntryRequestDTO.hoursWorked()));
        existingEntry.setDescription(timeSheetEntryRequestDTO.description());

        TimeSheetEntry updatedEntry = timeSheetEntryRepository.save(existingEntry);
        return convertToResponseDTO(updatedEntry);
    }

    @Override
    public boolean deleteTimeSheetEntry(Integer id) {
        if (!timeSheetEntryRepository.existsById(id)) {
            throw new RuntimeException("TimeSheetEntry not found with id: " + id);
        }
        timeSheetEntryRepository.deleteById(id);
        return true;
    }

    // Helper method to convert entity to response DTO
    private TimeSheetEntryResponseDTO convertToResponseDTO(TimeSheetEntry timeSheetEntry) {
        return new TimeSheetEntryResponseDTO(
                timeSheetEntry.getEntryId(),
                timeSheetEntry.getTimeSheet() != null ? timeSheetEntry.getTimeSheet().getTimesheetId() : null,
                timeSheetEntry.getTask() != null ? timeSheetEntry.getTask().getTaskId() : null,
                timeSheetEntry.getWorkDate(),
                timeSheetEntry.getHoursWorked() != null ? timeSheetEntry.getHoursWorked().doubleValue() : null,
                timeSheetEntry.getDescription()
        );
    }
}
