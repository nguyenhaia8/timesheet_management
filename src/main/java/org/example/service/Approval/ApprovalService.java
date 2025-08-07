package org.example.service.Approval;

import java.util.List;

import org.example.dto.request.ApprovalRequestDTO;
import org.example.dto.response.ApprovalResponseDTO;

public interface ApprovalService {
    ApprovalResponseDTO save(ApprovalRequestDTO approvalRequestDTO);
    List<ApprovalResponseDTO> findAll();
    ApprovalResponseDTO findById(Integer id);
    ApprovalResponseDTO update(Integer id, ApprovalRequestDTO approvalRequestDTO);
    void deleteById(Integer id);
    List<ApprovalResponseDTO> findByApprovedByEmployeeId(Integer employeeId);
}
