package org.example.service.TimeSheet;

import java.util.List;
import java.time.LocalDate;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.request.TimeSheetWithEntriesRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.dto.response.TimeSheetDetailResponseDTO;

public interface TimeSheetService {
    List<TimeSheetResponseDTO> findAll();
    TimeSheetResponseDTO findById(Integer id);
    TimeSheetResponseDTO save(TimeSheetRequestDTO timeSheetRequestDTO);
    TimeSheetResponseDTO saveWithEntries(TimeSheetWithEntriesRequestDTO timeSheetWithEntriesRequestDTO);
    TimeSheetResponseDTO update(Integer id, TimeSheetRequestDTO timeSheetRequestDTO);
    void deleteById(Integer id);
    TimeSheetDetailResponseDTO findDetailById(Integer id);
    List<TimeSheetResponseDTO> findByEmployeeId(Integer employeeId);
    List<TimeSheetResponseDTO> findByEmployeeIdAndPeriod(Integer employeeId, LocalDate periodStart, LocalDate periodEnd);
}
