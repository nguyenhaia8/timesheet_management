package org.example.service.TimeSheet.impl;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.model.TimeSheet;
import org.example.model.Employee;
import org.example.repository.TimeSheetRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.TimeSheet.TimeSheetService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TimeSheetServiceImpl implements TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;

    public TimeSheetServiceImpl(TimeSheetRepository timeSheetRepository, EmployeeRepository employeeRepository) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
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

        TimeSheet timeSheet = new TimeSheet(
                employee,
                dto.periodStartDate(),
                dto.periodEndDate()
        );

        if (dto.status() != null) {
            timeSheet.setStatus(TimeSheet.TimeSheetStatus.valueOf(dto.status().toUpperCase()));
        }
        if (dto.totalHours() != null) {
            timeSheet.setTotalHours(dto.totalHours());
        }

        TimeSheet saved = timeSheetRepository.save(timeSheet);
        return toTimeSheetResponseDTO(saved);
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
        
        if (dto.status() != null) {
            existingTimeSheet.setStatus(TimeSheet.TimeSheetStatus.valueOf(dto.status().toUpperCase()));
        }
        if (dto.totalHours() != null) {
            existingTimeSheet.setTotalHours(dto.totalHours());
        }

        TimeSheet updated = timeSheetRepository.save(existingTimeSheet);
        return toTimeSheetResponseDTO(updated);
    }

    @Override
    public void deleteById(Integer id) {
        timeSheetRepository.deleteById(id);
    }

    private TimeSheetResponseDTO toTimeSheetResponseDTO(TimeSheet timeSheet) {
        return new TimeSheetResponseDTO(
                timeSheet.getTimesheetId(),
                timeSheet.getEmployee() != null ? timeSheet.getEmployee().getEmployeeId() : null,
                timeSheet.getEmployee() != null ? 
                    timeSheet.getEmployee().getFirstName() + " " + timeSheet.getEmployee().getLastName() : null,
                timeSheet.getPeriodStartDate(),
                timeSheet.getPeriodEndDate(),
                timeSheet.getStatus() != null ? timeSheet.getStatus().name() : null,
                timeSheet.getSubmissionDate(),
                timeSheet.getTotalHours()
        );
    }
}
