package org.example.controller;

import java.util.List;

import org.example.dto.request.TimeSheetEntryRequestDTO;
import org.example.dto.response.TimeSheetEntryResponseDTO;
import org.example.service.TimeSheetEntry.TimeSheetEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timesheet-entries")
public class TimeSheetEntryController {

    @Autowired
    private TimeSheetEntryService timeSheetEntryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TimeSheetEntryResponseDTO> createTimeSheetEntry(@RequestBody TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        try {
            TimeSheetEntryResponseDTO createdEntry = timeSheetEntryService.save(timeSheetEntryRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TimeSheetEntryResponseDTO>> getAllTimeSheetEntries() {
        try {
            List<TimeSheetEntryResponseDTO> entries = timeSheetEntryService.findAll();
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TimeSheetEntryResponseDTO> getTimeSheetEntryById(@PathVariable Integer id) {
        try {
            TimeSheetEntryResponseDTO entry = timeSheetEntryService.findById(id);
            if (entry != null) {
                return ResponseEntity.ok(entry);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TimeSheetEntryResponseDTO> updateTimeSheetEntry(
            @PathVariable Integer id,
            @RequestBody TimeSheetEntryRequestDTO timeSheetEntryRequestDTO) {
        try {
            TimeSheetEntryResponseDTO updatedEntry = timeSheetEntryService.update(id, timeSheetEntryRequestDTO);
            return ResponseEntity.ok(updatedEntry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Object> deleteTimeSheetEntry(@PathVariable Integer id) {
        try {
            timeSheetEntryService.deleteById(id);
            return ResponseEntity.ok(new DeleteResponse(true, "TimeSheetEntry deleted successfully"));
        } catch (RuntimeException e) {
            // Check if it's a business rule violation (parent timesheet not DRAFT)
            if (e.getMessage() != null && e.getMessage().contains("Cannot delete timesheet entry. Parent timesheet has status")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DeleteResponse(false, e.getMessage()));
            }
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
