package org.example.controller;

import org.example.dto.request.TimeSheetRequestDTO;
import org.example.dto.request.TimeSheetWithEntriesRequestDTO;
import org.example.dto.response.TimeSheetResponseDTO;
import org.example.dto.response.MessageResponseDTO;
import org.example.dto.response.TimeSheetDetailResponseDTO;
import org.example.service.TimeSheet.TimeSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/timesheets")
@CrossOrigin(
        origins = {
            "http://localhost:3000",
            "http://localhost:3001",
            "http://localhost:3002",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:3001",
            "http://127.0.0.1:3002"
        },
        allowedHeaders = "*",
        allowCredentials = "true",
        maxAge = 3600
)
public class TimeSheetController {

    private final TimeSheetService timeSheetService;

    @Autowired
    public TimeSheetController(TimeSheetService timeSheetService) {
        this.timeSheetService = timeSheetService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TimeSheetResponseDTO> createTimeSheet(@RequestBody TimeSheetRequestDTO timeSheetRequestDTO) {
        try {
            TimeSheetResponseDTO createdTimeSheet = timeSheetService.save(timeSheetRequestDTO);
            return new ResponseEntity<>(createdTimeSheet, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/with-entries")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<TimeSheetResponseDTO> createTimeSheetWithEntries(@RequestBody TimeSheetWithEntriesRequestDTO timeSheetWithEntriesRequestDTO) {
        try {
            TimeSheetResponseDTO createdTimeSheet = timeSheetService.saveWithEntries(timeSheetWithEntriesRequestDTO);
            return new ResponseEntity<>(createdTimeSheet, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<TimeSheetResponseDTO>> getAllTimeSheets() {
        try {
            List<TimeSheetResponseDTO> timeSheets = timeSheetService.findAll();
            return new ResponseEntity<>(timeSheets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == authentication.principal.employee.employeeId")
    public ResponseEntity<TimeSheetResponseDTO> getTimeSheetById(@PathVariable Integer id) {
        try {
            TimeSheetResponseDTO timeSheet = timeSheetService.findById(id);
            if (timeSheet != null) {
                return new ResponseEntity<>(timeSheet, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/detail")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == authentication.principal.employee.employeeId")
    public ResponseEntity<TimeSheetDetailResponseDTO> getTimeSheetDetailById(@PathVariable Integer id) {
        try {
            TimeSheetDetailResponseDTO timeSheetDetail = timeSheetService.findDetailById(id);
            return new ResponseEntity<>(timeSheetDetail, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<TimeSheetResponseDTO>> getTimeSheetsByCurrentUser() {
        try {
            // Get the authenticated user's employeeId from the security context
            org.springframework.security.core.Authentication authentication = 
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            
            // Cast to our User class to get employee information
            org.example.model.User user = (org.example.model.User) authentication.getPrincipal();
            Integer employeeId = user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null;
            
            if (employeeId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            
            List<TimeSheetResponseDTO> timeSheets = timeSheetService.findByEmployeeId(employeeId);
            return new ResponseEntity<>(timeSheets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> getTimeSheetsByEmployeeAndPeriod(
            @PathVariable Integer employeeId,
            @RequestParam(required = false) LocalDate periodStart,
            @RequestParam(required = false) LocalDate periodEnd) {
        try {
            List<TimeSheetResponseDTO> timeSheets;
            if (periodStart != null && periodEnd != null) {
                timeSheets = timeSheetService.findByEmployeeIdAndPeriod(employeeId, periodStart, periodEnd);
                if (timeSheets == null || timeSheets.isEmpty()) {
                    String msg = String.format("No timesheets found for employeeId %d between %s and %s",
                            employeeId, periodStart, periodEnd);
                    return new ResponseEntity<>(new MessageResponseDTO(msg), HttpStatus.NOT_FOUND);
                }
            } else {
                timeSheets = timeSheetService.findByEmployeeId(employeeId);
            }
            
            return new ResponseEntity<List<TimeSheetResponseDTO>>(timeSheets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == authentication.principal.employee.employeeId")
    public ResponseEntity<TimeSheetResponseDTO> updateTimeSheet(@PathVariable Integer id, @RequestBody TimeSheetRequestDTO timeSheetRequestDTO) {
        try {
            TimeSheetResponseDTO updatedTimeSheet = timeSheetService.update(id, timeSheetRequestDTO);
            return new ResponseEntity<>(updatedTimeSheet, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Object> deleteTimeSheet(@PathVariable Integer id) {
        try {
            timeSheetService.deleteById(id);
            return ResponseEntity.ok(new DeleteResponse(true, "TimeSheet deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "TimeSheet not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponse(false, "Error deleting TimeSheet"));
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
