package org.example.controller;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;
import org.example.service.Approval.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    @Autowired
    private ApprovalService approvalService;

    // Get all approvals
    @GetMapping
    public ResponseEntity<List<ApprovalResponseDTO>> getAllApprovals() {
        try {
            List<ApprovalResponseDTO> approvals = approvalService.getAllApprovals();
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get approval by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalResponseDTO> getApprovalById(@PathVariable Integer id) {
        try {
            ApprovalResponseDTO approval = approvalService.getApprovalById(id);
            return ResponseEntity.ok(approval);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Create new approval
    @PostMapping
    public ResponseEntity<ApprovalResponseDTO> createApproval(@RequestBody ApprovalRequestDTO approvalRequestDTO) {
        try {
            ApprovalResponseDTO created = approvalService.createApproval(approvalRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update approval
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalResponseDTO> updateApproval(@PathVariable Integer id, @RequestBody ApprovalRequestDTO approvalRequestDTO) {
        try {
            ApprovalResponseDTO updated = approvalService.updateApproval(id, approvalRequestDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete approval
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteApproval(@PathVariable Integer id) {
        try {
            boolean deleted = approvalService.deleteApproval(id);
            if (deleted) {
                return ResponseEntity.ok(new DeleteResponse(true, "Approval deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "Approval not found"));
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DeleteResponse(false, "Approval not found"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DeleteResponse(false, "Error deleting Approval"));
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
