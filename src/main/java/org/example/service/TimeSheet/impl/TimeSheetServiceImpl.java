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
import org.example.repository.ApprovalRepository;
import org.example.service.TimeSheet.TimeSheetService;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSheetServiceImpl implements TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;
    private final TimeSheetEntryService timeSheetEntryService;
    private final ApprovalRepository approvalRepository;

    public TimeSheetServiceImpl(TimeSheetRepository timeSheetRepository, 
                              EmployeeRepository employeeRepository,
                              TimeSheetEntryService timeSheetEntryService,
                              ApprovalRepository approvalRepository) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
        this.timeSheetEntryService = timeSheetEntryService;
        this.approvalRepository = approvalRepository;
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
        // Ensure non-null timestamps for DB constraints
        timeSheet.setCreatedAt(LocalDateTime.now());
        timeSheet.setUpdatedAt(LocalDateTime.now());

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
        // Auto-fill submissionDate if not provided
        timeSheet.setSubmissionDate(dto.submissionDate() != null ? dto.submissionDate() : LocalDateTime.now());

        // Calculate total hours from entries if not provided
        BigDecimal computedTotal = BigDecimal.ZERO;
        if (dto.timeSheetEntries() != null && !dto.timeSheetEntries().isEmpty()) {
            for (TimeSheetEntryRequestDTO entryDto : dto.timeSheetEntries()) {
                if (entryDto.hoursWorked() != null) {
                    computedTotal = computedTotal.add(entryDto.hoursWorked());
                }
            }
        }
        timeSheet.setTotalHours(dto.totalHours() != null ? dto.totalHours() : computedTotal);
        // Ensure non-null timestamps for DB constraints
        timeSheet.setCreatedAt(LocalDateTime.now());
        timeSheet.setUpdatedAt(LocalDateTime.now());

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
    @Transactional
    public TimeSheetResponseDTO updateWithEntries(Integer id, TimeSheetWithEntriesRequestDTO dto) {
        TimeSheet existingTimeSheet = timeSheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + id));

        // Check if timesheet can be updated (only DRAFT status can be updated)
        if (existingTimeSheet.getStatus() != TimeSheet.TimeSheetStatus.DRAFT) {
            throw new RuntimeException("Cannot update timesheet with status: " + existingTimeSheet.getStatus() + ". Only DRAFT timesheets can be updated.");
        }

        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + dto.employeeId()));

        // Update timesheet basic information
        existingTimeSheet.setEmployee(employee);
        existingTimeSheet.setPeriodStartDate(dto.periodStartDate());
        existingTimeSheet.setPeriodEndDate(dto.periodEndDate());
        
        if (dto.status() != null) {
            existingTimeSheet.setStatus(TimeSheet.TimeSheetStatus.valueOf(dto.status().toUpperCase()));
        }
        
        if (dto.submissionDate() != null) {
            existingTimeSheet.setSubmissionDate(dto.submissionDate());
        }
        
        if (dto.totalHours() != null) {
            existingTimeSheet.setTotalHours(dto.totalHours());
        }

        TimeSheet updatedTimeSheet = timeSheetRepository.save(existingTimeSheet);

        // Delete existing entries and create new ones
        timeSheetEntryService.deleteByTimesheetId(id);
        
        // Create and save new timesheet entries
        if (dto.timeSheetEntries() != null && !dto.timeSheetEntries().isEmpty()) {
            for (TimeSheetEntryRequestDTO entryDto : dto.timeSheetEntries()) {
                TimeSheetEntryRequestDTO newEntryDto = new TimeSheetEntryRequestDTO(
                    updatedTimeSheet.getTimesheetId(),
                    entryDto.date(),
                    entryDto.projectId(),
                    entryDto.taskDescription(),
                    entryDto.hoursWorked()
                );
                timeSheetEntryService.save(newEntryDto);
            }
        }

        return toTimeSheetResponseDTO(updatedTimeSheet);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        TimeSheet timeSheet = timeSheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Timesheet not found with id: " + id));
        
        // Check if timesheet can be deleted (only DRAFT status can be deleted)
        if (timeSheet.getStatus() != TimeSheet.TimeSheetStatus.DRAFT) {
            throw new RuntimeException("Cannot delete timesheet with status: " + timeSheet.getStatus() + ". Only DRAFT timesheets can be deleted.");
        }
        
        // Delete related approvals first
        approvalRepository.deleteByTimesheetTimesheetId(id);
        
        // Delete related timesheet entries
        timeSheetEntryService.deleteByTimesheetId(id);
        
        // Delete the timesheet
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

    @Override
    public List<TimeSheetResponseDTO> findByEmployeeIdAndPeriod(Integer employeeId, LocalDate periodStart, LocalDate periodEnd) {
        return timeSheetRepository.findByEmployeeEmployeeIdAndPeriodStartDateBetween(employeeId, periodStart, periodEnd)
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
