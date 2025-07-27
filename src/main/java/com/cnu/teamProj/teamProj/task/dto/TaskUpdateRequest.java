package com.cnu.teamProj.teamProj.task.dto;

public class TaskUpdateRequest {
    private String description;
    private String assigneeId;

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Integer assigneeId) {
        this.assigneeId = String.valueOf(assigneeId);
    }
}

