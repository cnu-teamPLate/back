package com.cnu.teamProj.teamProj.task.dto;

import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TaskDTO {
    @Schema(description = "과제 아이디 (⚠️업로드 시 필수값 아님)")
    private int taskId; // DB에서 `TASK_ID`는 int 타입
    @Schema(description = "유저 아이디", example = "20241121")
    private String id; // 소문자로 수정 (DB 컬럼명과 맞춤)
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "역할", example = "개발")
    private String role;
    @Schema(description = "과제 항목", example = "개발")
    private String cate;
    @Schema(description = "과제 난이도 (1부터 3까지)", example = "3")
    @Min(1) @Max(3)
    private int level;
    @Schema(description = "마감 기한", example = "2025-01-14T00:02:27.000Z", type = "string")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss.000Z")
    private ZonedDateTime date; // `deadline` → `date`로 수정 (DB 컬럼명과 맞춤)
    @Schema(description = "과제 설명")
    private String detail;
    @Schema(description = "완료 여부 체크, 1이면 완료", example = "0")
    private int checkBox; // `checkbox` → `checkBox`로 수정
    @Schema(description = "과제 명", example = "로그인 기능 개발")
    private String taskName;
    @Schema(description = "유저 명 (⚠️과제 업로드 시 불필요)")
    private String userName;
    @Schema(description = "과제에 업로드 된 파일 정보 (⚠️과제 업로드 시 불필요)")
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
