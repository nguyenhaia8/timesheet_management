package org.example.controller;

import java.util.List;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheet-entries")
public class TimeSheetEntryController {

    @Autowired
    private TimeSheetEntryService timeSheetEntryService;

    // Get all timesheet entries
    @GetMapping
    public ResponseEntity<List<TimeSheetEntryResponseDTO>> getAllTimeSheetEntries() {
        try {
            List<TimeSheetEntryResponseDTO> entries = timeSheetEntryService.getAllTimeSheetEntries();
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get timesheet entry by ID
    @GetMapping("/{id}")
    public ResponseEntity<TimeSheetEntryResponseDTO> getTimeSheetEntryById(@PathVariable Integer id) {
        try {
            TimeSheetEntryResponseDTO entry = timeSheetEntryService.getTimeSheetEntryById(id);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new timesheet entry
    @PostMapping
    public ResponseEntity<TimeSheetEntryResponseDTO> createTimeSheetEntry(@RequestBody TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        try {
            TimeSheetEntryResponseDTO createdEntry = timeSheetEntryService.createTimeSheetEntry(timeSheetEntryRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update timesheet entry
    @PutMapping("/{id}")
    public ResponseEntity<TimeSheetEntryResponseDTO> updateTimeSheetEntry(
            @PathVariable Integer id,
            @RequestBody TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        try {
            TimeSheetEntryResponseDTO updatedEntry = timeSheetEntryService.updateTimeSheetEntry(id, timeSheetEntryRequestDTO);
            return ResponseEntity.ok(updatedEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete timesheet entry
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteTimeSheetEntry(@PathVariable Integer id) {
        try {
            boolean deleted = timeSheetEntryService.deleteTimeSheetEntry(id);
            if (deleted) {
                return ResponseEntity.ok(new DeleteResponse(true, "TimeSheetEntry deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "TimeSheetEntry not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "TimeSheetEntry not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponse(false, "Error deleting TimeSheetEntry"));
        }
    }

    // Inner class for delete response
    public static class DeleteResponse {
        private boolean status;
        private String message;

        public DeleteResponse(boolean status, String message) {
            this.status = status;
            this.message = message;
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
