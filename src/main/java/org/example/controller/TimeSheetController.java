package org.example.controller;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.service.TimeSheet.TimeSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/timesheets")
public class TimeSheetController {

    private final TimeSheetService timeSheetService;

    @Autowired
    public TimeSheetController(TimeSheetService timeSheetService) {
        this.timeSheetService = timeSheetService;
    }

    @GetMapping
    public ResponseEntity<List<TimeSheetResponseDTO>> getAllTimeSheets() {
        try {
            List<TimeSheetResponseDTO> timeSheets = timeSheetService.getAllTimeSheets();
            return new ResponseEntity<>(timeSheets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeSheetResponseDTO> getTimeSheetById(@PathVariable Integer id) {
        try {
            TimeSheetResponseDTO timeSheet = timeSheetService.getTimeSheetById(id);
            if (timeSheet != null) {
                return new ResponseEntity<>(timeSheet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<TimeSheetResponseDTO> createTimeSheet(@RequestBody TimeSheetRequestDTO timeSheetRequestDTO) {
        try {
            TimeSheetResponseDTO createdTimeSheet = timeSheetService.createTimeSheet(timeSheetRequestDTO);
            if (createdTimeSheet != null) {
                return new ResponseEntity<>(createdTimeSheet, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeSheetResponseDTO> updateTimeSheet(@PathVariable Integer id, @RequestBody TimeSheetRequestDTO timeSheetRequestDTO) {
        try {
            TimeSheetResponseDTO updatedTimeSheet = timeSheetService.updateTimeSheet(id, timeSheetRequestDTO);
            if (updatedTimeSheet != null) {
                return new ResponseEntity<>(updatedTimeSheet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTimeSheet(@PathVariable Integer id) {
        try {
            boolean deleted = timeSheetService.deleteTimeSheet(id);
            if (deleted) {
                return ResponseEntity.ok().body(Map.of(
                    "status", true,
                    "message", "TimeSheet deleted successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "status", false,
                    "message", "TimeSheet not found"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", false,
                "message", "Delete failed"
            ));
        }
    }
}
