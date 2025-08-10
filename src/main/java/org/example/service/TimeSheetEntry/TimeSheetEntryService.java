package org.example.service.TimeSheetEntry;

import java.util.List;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;

public interface TimeSheetEntryService {
    List<TimeSheetEntryResponseDTO> findAll();
    TimeSheetEntryResponseDTO findById(Integer id);
    TimeSheetEntryResponseDTO save(TimeSheetEntryRequestDTO timeSheetEntryRequestDTO);
    TimeSheetEntryResponseDTO update(Integer id, TimeSheetEntryRequestDTO timeSheetEntryRequestDTO);
    void deleteById(Integer id);
    void deleteByTimesheetId(Integer timesheetId);
    List<TimeSheetEntryResponseDTO> findByTimesheetId(Integer timesheetId);
}
