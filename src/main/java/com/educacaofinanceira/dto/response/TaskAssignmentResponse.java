package com.educacaofinanceira.dto.response;

import com.educacaofinanceira.model.TaskAssignment;
import com.educacaofinanceira.model.enums.AssignmentStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TaskAssignmentResponse {

    private UUID id;
    private TaskResponse task;
    private UUID childId;
    private String childName;
    private AssignmentStatus status;
    private LocalDateTime completedAt;
    private LocalDateTime approvedAt;
    private String approvedByName;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public static TaskAssignmentResponse fromAssignment(TaskAssignment assignment) {
        TaskAssignmentResponse response = new TaskAssignmentResponse();
        response.setId(assignment.getId());
        response.setTask(TaskResponse.fromTask(assignment.getTask()));
        response.setChildId(assignment.getAssignedToChild().getId());
        response.setChildName(assignment.getAssignedToChild().getFullName());
        response.setStatus(assignment.getStatus());
        response.setCompletedAt(assignment.getCompletedAt());
        response.setApprovedAt(assignment.getApprovedAt());
        if (assignment.getApprovedBy() != null) {
            response.setApprovedByName(assignment.getApprovedBy().getFullName());
        }
        response.setRejectionReason(assignment.getRejectionReason());
        response.setCreatedAt(assignment.getCreatedAt());
        return response;
    }
}
