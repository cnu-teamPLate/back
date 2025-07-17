package com.cnu.teamProj.teamProj.task.dto;

import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.task.entity.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Getter
@Setter
@Schema(description = "과제에 대한 디테일한 정보")
public class DetailTaskDto {
    @Schema(description = "과제 아이디")
    private int taskId; // DB에서 `TASK_ID`는 int 타입
    @Schema(description = "유저 학번 정보")
    private String id; // 소문자로 수정 (DB 컬럼명과 맞춤)
    @Schema(description = "유저 이름")
    private String userName;
    @Schema(description = "프로젝트 아이디")
    private String projId;
    @Schema(description = "프로젝트 명")
    private String projName;
    @Schema(description = "유저 역할")
    private String role;
    @Schema(description = "과제 카테고리")
    private String cate;
    @Schema(description = "과제 난이도")
    private int level;
    @Schema(description = "마감일")
    private String date; // `deadline` → `date`로 수정 (DB 컬럼명과 맞춤)
    @Schema(description = "디테일한 정보")
    private String detail;
    @Schema(description = "완료 여부")
    private int checkBox; // `checkbox` → `checkBox`로 수정
    @Schema(description = "과제 이름")
    private String taskName;
    @Schema(description = "과제에 등록된 파일 정보")
    private List<FileDto> files;

    public DetailTaskDto(Task task) { //username, projName, files 값은 추가로 할당해줘야 함.
        taskId = task.getTaskId();
        id = task.getId();
        projId = task.getProjId();
        role = task.getRole();
        cate = task.getCate();
        level = task.getLevel();
        date = task.getDate().toString();
        detail = task.getDetail();
        checkBox = task.getCheckBox();
        taskName = task.getTaskName();
    }
}
