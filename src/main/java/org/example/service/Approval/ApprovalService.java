package org.example.service.Approval;

import java.util.List;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;

public interface ApprovalService {
    List<ApprovalResponseDTO> getAllApprovals();
    ApprovalResponseDTO getApprovalById(Integer id);
    ApprovalResponseDTO createApproval(ApprovalRequestDTO approvalRequestDTO);
    ApprovalResponseDTO updateApproval(Integer id, ApprovalRequestDTO approvalRequestDTO);
    boolean deleteApproval(Integer id);
}
