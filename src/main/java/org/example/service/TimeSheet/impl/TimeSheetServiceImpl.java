package org.example.service.TimeSheet.impl;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.model.TimeSheet;
import org.example.model.Employee;
import org.example.repository.TimeSheetRepository;
import org.example.repository.EmployeeRepository;
import org.example.service.TimeSheet.TimeSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class TimeSheetServiceImpl implements TimeSheetService {

    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public TimeSheetServiceImpl(TimeSheetRepository timeSheetRepository, EmployeeRepository employeeRepository) {
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public List<TimeSheetResponseDTO> getAllTimeSheets() {
        return timeSheetRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TimeSheetResponseDTO getTimeSheetById(Integer id) {
        Optional<TimeSheet> timeSheet = timeSheetRepository.findById(id);
        return timeSheet.map(this::toResponseDTO).orElse(null);
    }

    @Override
    public TimeSheetResponseDTO createTimeSheet(TimeSheetRequestDTO dto) {
        Employee employee = employeeRepository.findById(dto.employeeId()).orElse(null);
        if (employee == null) return null;

        TimeSheet timeSheet = new TimeSheet(
                employee,
                dto.weekStartDate(),
                dto.totalHours() != null ? BigDecimal.valueOf(dto.totalHours()) : BigDecimal.ZERO,
                dto.status() != null ? dto.status() : "DRAFT",
                dto.submittedDate()
        );
        TimeSheet saved = timeSheetRepository.save(timeSheet);
        return toResponseDTO(saved);
    }

    @Override
    public TimeSheetResponseDTO updateTimeSheet(Integer id, TimeSheetRequestDTO dto) {
        Optional<TimeSheet> existing = timeSheetRepository.findById(id);
        if (existing.isEmpty()) return null;

        TimeSheet timeSheet = existing.get();
        Employee employee = employeeRepository.findById(dto.employeeId()).orElse(null);
        if (employee != null) timeSheet.setEmployee(employee);
        if (dto.weekStartDate() != null) timeSheet.setWeekStartDate(dto.weekStartDate());
        if (dto.totalHours() != null) timeSheet.setTotalHours(BigDecimal.valueOf(dto.totalHours()));
        if (dto.status() != null) timeSheet.setStatus(dto.status());
        if (dto.submittedDate() != null) timeSheet.setSubmittedDate(dto.submittedDate());

        TimeSheet updated = timeSheetRepository.save(timeSheet);
        return toResponseDTO(updated);
    }

    @Override
    public boolean deleteTimeSheet(Integer id) {
        Optional<TimeSheet> timeSheet = timeSheetRepository.findById(id);
        if (timeSheet.isPresent()) {
            timeSheetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TimeSheetResponseDTO toResponseDTO(TimeSheet timeSheet) {
        return new TimeSheetResponseDTO(
                timeSheet.getTimesheetId(),
                timeSheet.getEmployee() != null ? timeSheet.getEmployee().getEmployeeId() : null,
                timeSheet.getWeekStartDate(),
                timeSheet.getTotalHours() != null ? timeSheet.getTotalHours().doubleValue() : 0.0,
                timeSheet.getStatus(),
                timeSheet.getSubmittedDate()
        );
    }
}
