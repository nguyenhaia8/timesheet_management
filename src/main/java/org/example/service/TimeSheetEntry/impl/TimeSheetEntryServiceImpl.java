package org.example.service.TimeSheetEntry.impl;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;
import org.example.model.Project;
import org.example.model.TimeSheet;
import org.example.model.TimeSheetEntry;
import org.example.repository.ProjectRepository;
import org.example.repository.TimeSheetEntryRepository;
import org.example.repository.TimeSheetRepository;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSheetEntryServiceImpl implements TimeSheetEntryService {

    private final TimeSheetEntryRepository timeSheetEntryRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final ProjectRepository projectRepository;

    public TimeSheetEntryServiceImpl(TimeSheetEntryRepository timeSheetEntryRepository,
                                   TimeSheetRepository timeSheetRepository,
                                   ProjectRepository projectRepository) {
        this.timeSheetEntryRepository = timeSheetEntryRepository;
        this.timeSheetRepository = timeSheetRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public TimeSheetEntryResponseDTO save(TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        TimeSheet timeSheet = timeSheetRepository.findById(timeSheetEntryRequestDTO.timesheetId())
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timeSheetEntryRequestDTO.timesheetId()));

        Project project = projectRepository.findById(timeSheetEntryRequestDTO.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + timeSheetEntryRequestDTO.projectId()));

        TimeSheetEntry timeSheetEntry = new TimeSheetEntry();
        timeSheetEntry.setTimesheet(timeSheet);
        timeSheetEntry.setDate(timeSheetEntryRequestDTO.date());
        timeSheetEntry.setProject(project);
        timeSheetEntry.setTaskDescription(timeSheetEntryRequestDTO.taskDescription());
        timeSheetEntry.setHoursWorked(timeSheetEntryRequestDTO.hoursWorked());

        TimeSheetEntry savedEntry = timeSheetEntryRepository.save(timeSheetEntry);
        return toTimeSheetEntryResponseDTO(savedEntry);
    }

    @Override
    public TimeSheetEntryResponseDTO update(Integer id, TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        TimeSheetEntry existingEntry = timeSheetEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet entry not found with id: " + id));

        TimeSheet timeSheet = timeSheetRepository.findById(timeSheetEntryRequestDTO.timesheetId())
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + timeSheetEntryRequestDTO.timesheetId()));

        Project project = projectRepository.findById(timeSheetEntryRequestDTO.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + timeSheetEntryRequestDTO.projectId()));

        existingEntry.setTimesheet(timeSheet);
        existingEntry.setDate(timeSheetEntryRequestDTO.date());
        existingEntry.setProject(project);
        existingEntry.setTaskDescription(timeSheetEntryRequestDTO.taskDescription());
        existingEntry.setHoursWorked(timeSheetEntryRequestDTO.hoursWorked());

        TimeSheetEntry updatedEntry = timeSheetEntryRepository.save(existingEntry);
        return toTimeSheetEntryResponseDTO(updatedEntry);
    }

    @Override
    public List<TimeSheetEntryResponseDTO> findAll() {
        return timeSheetEntryRepository.findAll().stream()
                .map(this::toTimeSheetEntryResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSheetEntryResponseDTO findById(Integer id) {
        TimeSheetEntry timeSheetEntry = timeSheetEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet entry not found with id: " + id));
        return toTimeSheetEntryResponseDTO(timeSheetEntry);
    }

    @Override
    public void deleteById(Integer id) {
        timeSheetEntryRepository.deleteById(id);
    }

    @Override
    public List<TimeSheetEntryResponseDTO> findByTimesheetId(Integer timesheetId) {
        return timeSheetEntryRepository.findByTimesheetTimesheetId(timesheetId).stream()
                .map(this::toTimeSheetEntryResponseDTO)
                .collect(Collectors.toList());
    }

    private TimeSheetEntryResponseDTO toTimeSheetEntryResponseDTO(TimeSheetEntry timeSheetEntry) {
        return new TimeSheetEntryResponseDTO(
                timeSheetEntry.getEntryId(),
                timeSheetEntry.getTimesheet() != null ? timeSheetEntry.getTimesheet().getTimesheetId() : null,
                timeSheetEntry.getDate(),
                timeSheetEntry.getProject() != null ? timeSheetEntry.getProject().getProjectId() : null,
                timeSheetEntry.getProject() != null ? timeSheetEntry.getProject().getName() : null,
                timeSheetEntry.getTaskDescription(),
                timeSheetEntry.getHoursWorked()
        );
    }
}
