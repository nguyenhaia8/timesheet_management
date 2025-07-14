package org.example.service.TimeSheet;

import java.util.List;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;

public interface TimeSheetService {
    List<TimeSheetResponseDTO> getAllTimeSheets();
    TimeSheetResponseDTO getTimeSheetById(Integer id);
    TimeSheetResponseDTO createTimeSheet(TimeSheetRequestDTO timeSheetRequestDTO);
    TimeSheetResponseDTO updateTimeSheet(Integer id, TimeSheetRequestDTO timeSheetRequestDTO);
    boolean deleteTimeSheet(Integer id);
}
