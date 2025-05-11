package com.cnu.teamProj.teamProj.task.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.ZonedDateTime;

@Getter
@Setter
public class TaskDTO {
    private int taskId; // DB에서 `TASK_ID`는 int 타입
    private String id; // 소문자로 수정 (DB 컬럼명과 맞춤)
    private String projId;
    private String role;
    private String cate;
    private int level;
    private ZonedDateTime date; // `deadline` → `date`로 수정 (DB 컬럼명과 맞춤)
    private String detail;
    private int checkBox; // `checkbox` → `checkBox`로 수정
    private String taskName;
}
