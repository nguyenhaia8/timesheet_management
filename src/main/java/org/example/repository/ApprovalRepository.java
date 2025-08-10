package org.example.repository;

import org.example.model.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Integer> {
    List<Approval> findByTimesheetTimesheetId(Integer timesheetId);
    List<Approval> findByApprovedByEmployeeId(Integer employeeId);
    List<Approval> findByStatus(Approval.ApprovalStatus status);
    Approval findByTimesheetTimesheetIdAndStatus(Integer timesheetId, Approval.ApprovalStatus status);
    void deleteByTimesheetTimesheetId(Integer timesheetId);
} 