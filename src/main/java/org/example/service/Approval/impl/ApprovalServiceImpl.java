package org.example.service.Approval.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;
import org.example.model.Approval;
import org.example.model.Employee;
import org.example.model.TimeSheet;
import org.example.repository.ApprovalRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.TimeSheetRepository;
import org.example.service.Approval.ApprovalService;
import org.springframework.stereotype.Service;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final TimeSheetRepository timeSheetRepository;
    private final EmployeeRepository employeeRepository;

    public ApprovalServiceImpl(ApprovalRepository approvalRepository,
                             TimeSheetRepository timeSheetRepository,
                             EmployeeRepository employeeRepository) {
        this.approvalRepository = approvalRepository;
        this.timeSheetRepository = timeSheetRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ApprovalResponseDTO save(ApprovalRequestDTO approvalRequestDTO) {
        TimeSheet timeSheet = timeSheetRepository.findById(approvalRequestDTO.timesheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + approvalRequestDTO.timesheetId()));
        
        Employee approvedBy = employeeRepository.findById(approvalRequestDTO.approvedBy())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + approvalRequestDTO.approvedBy()));

        Approval approval = new Approval(timeSheet, approvedBy);
        
        if (approvalRequestDTO.status() != null) {
            approval.setStatus(Approval.ApprovalStatus.valueOf(approvalRequestDTO.status().toUpperCase()));
        }
        
        if (approvalRequestDTO.comments() != null) {
            approval.setComments(approvalRequestDTO.comments());
        }

        Approval saved = approvalRepository.save(approval);
        return toApprovalResponseDTO(saved);
    }

    @Override
    public List<ApprovalResponseDTO> findAll() {
        return approvalRepository.findAll()
                .stream()
                .map(this::toApprovalResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ApprovalResponseDTO findById(Integer id) {
        return approvalRepository.findById(id)
                .map(this::toApprovalResponseDTO)
                .orElse(null);
    }

    @Override
    public ApprovalResponseDTO update(Integer id, ApprovalRequestDTO approvalRequestDTO) {
        Approval existingApproval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));

        TimeSheet timeSheet = timeSheetRepository.findById(approvalRequestDTO.timesheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + approvalRequestDTO.timesheetId()));
        
        Employee approvedBy = employeeRepository.findById(approvalRequestDTO.approvedBy())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + approvalRequestDTO.approvedBy()));

        existingApproval.setTimesheet(timeSheet);
        existingApproval.setApprovedBy(approvedBy);
        
        if (approvalRequestDTO.status() != null) {
            existingApproval.setStatus(Approval.ApprovalStatus.valueOf(approvalRequestDTO.status().toUpperCase()));
        }
        
        if (approvalRequestDTO.comments() != null) {
            existingApproval.setComments(approvalRequestDTO.comments());
        }

        // Set approvedAt timestamp when status changes to APPROVED or REJECTED
        if (approvalRequestDTO.status() != null && 
            (approvalRequestDTO.status().equalsIgnoreCase("APPROVED") || 
             approvalRequestDTO.status().equalsIgnoreCase("REJECTED"))) {
            existingApproval.setApprovedAt(LocalDateTime.now());
        }

        Approval updated = approvalRepository.save(existingApproval);
        return toApprovalResponseDTO(updated);
    }

    @Override
    public void deleteById(Integer id) {
        approvalRepository.deleteById(id);
    }

    @Override
    public List<ApprovalResponseDTO> findByApprovedByEmployeeId(Integer employeeId) {
        return approvalRepository.findByApprovedByEmployeeId(employeeId)
                .stream()
                .map(this::toApprovalResponseDTO)
                .collect(Collectors.toList());
    }

    private ApprovalResponseDTO toApprovalResponseDTO(Approval approval) {
        return new ApprovalResponseDTO(
                approval.getApprovalId(),
                approval.getTimesheet() != null ? approval.getTimesheet().getTimesheetId() : null,
                approval.getApprovedBy() != null ? approval.getApprovedBy().getEmployeeId() : null,
                approval.getApprovedBy() != null ? 
                    approval.getApprovedBy().getFirstName() + " " + approval.getApprovedBy().getLastName() : null,
                approval.getApprovedAt(),
                approval.getStatus() != null ? approval.getStatus().name() : null,
                approval.getComments()
        );
    }
}
