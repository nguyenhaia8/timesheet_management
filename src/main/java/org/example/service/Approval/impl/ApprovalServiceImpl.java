package org.example.service.Approval.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;
import org.example.model.Approval;
import org.example.model.Approval.ApprovalStatus;
import org.example.model.Employee;
import org.example.model.TimeSheet;
import org.example.repository.ApprovalRepository;
import org.example.repository.EmployeeRepository;
import org.example.repository.TimeSheetRepository;
import org.example.service.Approval.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalServiceImpl implements ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<ApprovalResponseDTO> getAllApprovals() {
        List<Approval> approvals = approvalRepository.findAll();
        return approvals.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public ApprovalResponseDTO getApprovalById(Integer id) {
        Approval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        return convertToResponseDTO(approval);
    }

    @Override
    public ApprovalResponseDTO createApproval(ApprovalRequestDTO approvalRequestDTO) {
        TimeSheet timeSheet = timeSheetRepository.findById(approvalRequestDTO.timeSheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + approvalRequestDTO.timeSheetId()));
        Employee approver = employeeRepository.findById(approvalRequestDTO.approverId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + approvalRequestDTO.approverId()));
        ApprovalStatus status = parseStatus(approvalRequestDTO.approvalStatus());
        LocalDateTime approvalDate = approvalRequestDTO.approvalDate() != null ? approvalRequestDTO.approvalDate().atStartOfDay() : null;
        Approval approval = new Approval(timeSheet, approver, status, approvalDate, approvalRequestDTO.comments());
        Approval saved = approvalRepository.save(approval);
        return convertToResponseDTO(saved);
    }

    @Override
    public ApprovalResponseDTO updateApproval(Integer id, ApprovalRequestDTO approvalRequestDTO) {
        Approval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found with id: " + id));
        TimeSheet timeSheet = timeSheetRepository.findById(approvalRequestDTO.timeSheetId())
                .orElseThrow(() -> new RuntimeException("TimeSheet not found with id: " + approvalRequestDTO.timeSheetId()));
        Employee approver = employeeRepository.findById(approvalRequestDTO.approverId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + approvalRequestDTO.approverId()));
        ApprovalStatus status = parseStatus(approvalRequestDTO.approvalStatus());
        LocalDateTime approvalDate = approvalRequestDTO.approvalDate() != null ? approvalRequestDTO.approvalDate().atStartOfDay() : null;
        approval.setTimeSheet(timeSheet);
        approval.setApprover(approver);
        approval.setApprovalStatus(status);
        approval.setApprovalDate(approvalDate);
        approval.setComments(approvalRequestDTO.comments());
        Approval updated = approvalRepository.save(approval);
        return convertToResponseDTO(updated);
    }

    @Override
    public boolean deleteApproval(Integer id) {
        if (!approvalRepository.existsById(id)) {
            throw new RuntimeException("Approval not found with id: " + id);
        }
        approvalRepository.deleteById(id);
        return true;
    }

    private ApprovalResponseDTO convertToResponseDTO(Approval approval) {
        LocalDate approvalDate = approval.getApprovalDate() != null ? approval.getApprovalDate().toLocalDate() : null;
        return new ApprovalResponseDTO(
                approval.getApprovalId(),
                approval.getTimeSheet() != null ? approval.getTimeSheet().getTimesheetId() : null,
                approval.getApprover() != null ? approval.getApprover().getEmployeeId() : null,
                approval.getApprovalStatus() != null ? approval.getApprovalStatus().name() : null,
                approvalDate,
                approval.getComments()
        );
    }

    private ApprovalStatus parseStatus(String status) {
        if (status == null) return ApprovalStatus.PENDING;
        try {
            return ApprovalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid approval status: " + status);
        }
    }
}
