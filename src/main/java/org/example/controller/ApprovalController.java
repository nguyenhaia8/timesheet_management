package org.example.controller;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;
import org.example.service.Approval.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ApprovalResponseDTO>> getAllApprovals() {
        try {
            List<ApprovalResponseDTO> approvals = approvalService.findAll();
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApprovalResponseDTO> getApprovalById(@PathVariable Integer id) {
        try {
            ApprovalResponseDTO approval = approvalService.findById(id);
            if (approval != null) {
                return ResponseEntity.ok(approval);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<ApprovalResponseDTO> createApproval(@RequestBody ApprovalRequestDTO approvalRequestDTO) {
        try {
            ApprovalResponseDTO created = approvalService.save(approvalRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApprovalResponseDTO> updateApproval(@PathVariable Integer id, @RequestBody ApprovalRequestDTO approvalRequestDTO) {
        try {
            ApprovalResponseDTO updated = approvalService.update(id, approvalRequestDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> deleteApproval(@PathVariable Integer id) {
        try {
            approvalService.deleteById(id);
            return ResponseEntity.ok(new DeleteResponse(true, "Approval deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "Approval not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponse(false, "Error deleting Approval"));
        }
    }

    @GetMapping("/my-approvals")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<List<ApprovalResponseDTO>> getMyApprovals() {
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
            
            List<ApprovalResponseDTO> approvals = approvalService.findByApprovedByEmployeeId(employeeId);
            return new ResponseEntity<>(approvals, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
