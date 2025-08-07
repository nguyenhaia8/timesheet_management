package org.example.service.TimeSheet.impl;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.request.TimeSheetWithEntriesRequestDTO;
import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.dto.response.TimeSheetDetailResponseDTO;
import org.example.model.TimeSheet;
import org.example.model.Employee;
import org.example.repository.TimeSheetRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.TimeSheet.TimeSheetService;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSheetServiceImpl implements TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;
    private final TimeSheetEntryService timeSheetEntryService;

    public TimeSheetServiceImpl(TimeSheetRepository timeSheetRepository, 
                              EmployeeRepository employeeRepository,
                              TimeSheetEntryService timeSheetEntryService) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
        this.timeSheetEntryService = timeSheetEntryService;
    }

    @Override
    public List<TimeSheetResponseDTO> findAll() {
        return timeSheetRepository.findAll()
                .stream()
                .map(this::toTimeSheetResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSheetResponseDTO findById(Integer id) {
        return timeSheetRepository.findById(id)
                .map(this::toTimeSheetResponseDTO)
                .orElse(null);
    }

    @Override
    public TimeSheetResponseDTO save(TimeSheetRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.employeeId()));

        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setEmployee(employee);
        timeSheet.setPeriodStartDate(dto.periodStartDate());
        timeSheet.setPeriodEndDate(dto.periodEndDate());
        timeSheet.setStatus(TimeSheet.TimeSheetStatus.DRAFT);
        timeSheet.setSubmissionDate(LocalDateTime.now());

        TimeSheet saved = timeSheetRepository.save(timeSheet);
        return toTimeSheetResponseDTO(saved);
    }

    @Override
    @Transactional
    public TimeSheetResponseDTO saveWithEntries(TimeSheetWithEntriesRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.employeeId()));

        // Create and save the timesheet
        TimeSheet timeSheet = new TimeSheet();
        timeSheet.setEmployee(employee);
        timeSheet.setPeriodStartDate(dto.periodStartDate());
        timeSheet.setPeriodEndDate(dto.periodEndDate());
        timeSheet.setStatus(TimeSheet.TimeSheetStatus.valueOf(dto.status()));
        timeSheet.setSubmissionDate(dto.submissionDate());
        timeSheet.setTotalHours(dto.totalHours());

        TimeSheet savedTimeSheet = timeSheetRepository.save(timeSheet);

        // Create and save timesheet entries
        if (dto.timeSheetEntries() != null && !dto.timeSheetEntries().isEmpty()) {
            for (TimeSheetEntryRequestDTO entryDto : dto.timeSheetEntries()) {
                TimeSheetEntryRequestDTO newEntryDto = new TimeSheetEntryRequestDTO(
                    savedTimeSheet.getTimesheetId(),
                    entryDto.date(),
                    entryDto.projectId(),
                    entryDto.taskDescription(),
                    entryDto.hoursWorked()
                );
                timeSheetEntryService.save(newEntryDto);
            }
        }

        return toTimeSheetResponseDTO(savedTimeSheet);
    }

    @Override
    public TimeSheetResponseDTO update(Integer id, TimeSheetRequestDTO dto) {
        TimeSheet existingTimeSheet = timeSheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + id));

        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.employeeId()));

        existingTimeSheet.setEmployee(employee);
        existingTimeSheet.setPeriodStartDate(dto.periodStartDate());
        existingTimeSheet.setPeriodEndDate(dto.periodEndDate());

        TimeSheet updated = timeSheetRepository.save(existingTimeSheet);
        return toTimeSheetResponseDTO(updated);
    }

    @Override
    public void deleteById(Integer id) {
        timeSheetRepository.deleteById(id);
    }

    @Override
    public TimeSheetDetailResponseDTO findDetailById(Integer id) {
        TimeSheet timeSheet = timeSheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + id));
        
        // Get timesheet entries for this timesheet
        var timeSheetEntries = timeSheetEntryService.findByTimesheetId(id);
        
        // Calculate total hours from entries
        BigDecimal calculatedTotalHours = timeSheetEntries.stream()
                .map(entry -> entry.hoursWorked())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new TimeSheetDetailResponseDTO(
                timeSheet.getTimesheetId(),
                timeSheet.getEmployee() != null ? timeSheet.getEmployee().getEmployeeId() : null,
                timeSheet.getEmployee() != null ? 
                    timeSheet.getEmployee().getFirstName() + " " + timeSheet.getEmployee().getLastName() : null,
                timeSheet.getPeriodStartDate(),
                timeSheet.getPeriodEndDate(),
                timeSheet.getStatus() != null ? timeSheet.getStatus().name() : null,
                timeSheet.getSubmissionDate(),
                timeSheet.getTotalHours(),
                timeSheetEntries,
                calculatedTotalHours
        );
    }

    @Override
    public List<TimeSheetResponseDTO> findByEmployeeId(Integer employeeId) {
        return timeSheetRepository.findByEmployeeEmployeeId(employeeId)
                .stream()
                .map(this::toTimeSheetResponseDTO)
                .collect(Collectors.toList());
    }

    private TimeSheetResponseDTO toTimeSheetResponseDTO(TimeSheet timeSheet) {
        // Get timesheet entries for this timesheet
        var timeSheetEntries = timeSheetEntryService.findByTimesheetId(timeSheet.getTimesheetId());
        
        // Calculate total hours from entries
        BigDecimal calculatedTotalHours = timeSheetEntries.stream()
                .map(entry -> entry.hoursWorked())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new TimeSheetResponseDTO(
                timeSheet.getTimesheetId(),
                timeSheet.getEmployee() != null ? timeSheet.getEmployee().getEmployeeId() : null,
                timeSheet.getEmployee() != null ? 
                    timeSheet.getEmployee().getFirstName() + " " + timeSheet.getEmployee().getLastName() : null,
                timeSheet.getPeriodStartDate(),
                timeSheet.getPeriodEndDate(),
                timeSheet.getStatus() != null ? timeSheet.getStatus().name() : null,
                timeSheet.getSubmissionDate(),
                timeSheet.getTotalHours(),
                timeSheetEntries,
                calculatedTotalHours
        );
    }
}
