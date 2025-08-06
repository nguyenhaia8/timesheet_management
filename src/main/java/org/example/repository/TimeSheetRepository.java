package org.example.repository;

import org.example.model.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSheetRepository extends JpaRepository<TimeSheet, Integer> {
    List<TimeSheet> findByEmployeeEmployeeId(Integer employeeId);
    List<TimeSheet> findByEmployeeEmployeeIdAndStatus(Integer employeeId, TimeSheet.TimeSheetStatus status);
    List<TimeSheet> findByPeriodStartDateBetween(LocalDate startDate, LocalDate endDate);
    List<TimeSheet> findByStatus(TimeSheet.TimeSheetStatus status);
} 