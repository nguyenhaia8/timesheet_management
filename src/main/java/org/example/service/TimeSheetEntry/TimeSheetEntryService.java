package org.example.service.TimeSheetEntry;

import java.util.List;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;

public interface TimeSheetEntryService {
    List<TimeSheetEntryResponseDTO> getAllTimeSheetEntries();
    TimeSheetEntryResponseDTO getTimeSheetEntryById(Integer id);
    TimeSheetEntryResponseDTO createTimeSheetEntry(TimeSheetEntryRequestDTO timeSheetEntryRequestDTO);
    TimeSheetEntryResponseDTO updateTimeSheetEntry(Integer id, TimeSheetEntryRequestDTO timeSheetEntryRequestDTO);
    boolean deleteTimeSheetEntry(Integer id);
}
