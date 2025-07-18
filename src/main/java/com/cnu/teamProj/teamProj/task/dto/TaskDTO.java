package com.cnu.teamProj.teamProj.task.dto;

import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.task.entity.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
    private String userName;
    private List<FileDto> files;

    public TaskDTO(Task task) {
        taskId = task.getTaskId();
        id = task.getId();
        projId = task.getProjId();
        role = task.getRole();
        cate = task.getCate();
        level = task.getLevel();
        date = task.getDate();
        detail = task.getDetail();
        checkBox = task.getCheckBox();
        taskName = task.getTaskName();
    }
}
