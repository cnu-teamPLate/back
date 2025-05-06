package com.cnu.teamProj.teamProj.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocsViewRequestDto {
    private String projId;
    private String userId;
    private String taskId = "-1000";

    public DocsViewRequestDto(String projId, String userId) {
        this.projId = projId;
        this.userId = userId;
    }
}
