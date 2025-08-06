package org.example.repository;

import org.example.model.TimeSheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Integer> {
    List<TimeSheetEntry> findByTimesheetTimesheetId(Integer timesheetId);
    List<TimeSheetEntry> findByProjectProjectId(Integer projectId);
    List<TimeSheetEntry> findByDateBetween(LocalDate startDate, LocalDate endDate);
    List<TimeSheetEntry> findByTimesheetTimesheetIdAndDate(Integer timesheetId, LocalDate date);
} 