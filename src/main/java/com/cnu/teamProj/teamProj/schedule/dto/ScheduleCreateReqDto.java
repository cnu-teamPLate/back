package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "새로운 스케줄 생성 시 요청 값")
public class ScheduleCreateReqDto {
    @Schema(description = "프로젝트 아이디", example = "CSE00011")
    private String projId;
    @Schema(description = "스케줄 일자", example = "2025-05-27T14:30:00", type="string")
    private LocalDateTime date;
    @Schema(description = "스케줄 명", example = "개발 환경 세팅 관련 회의")
    private String scheName;
    @Schema(description = "스케줄 진행 장소", example = "서강대학교 P관 라운지")
    private String place;
    @Schema(description = "스케줄 종류<br/>🤝회의 = meeting<br/>📜과제 = task<br/>📅일정 = plan", example = "meeting")
    private String category;
    @Schema(description = "스케줄 설명", example = "회의 안건 : DB를 어떤 것을 사용할지, 배포 일정 관련 하여 회의 진행")
    private String detail;
    @Schema(description = "스케줄 참여자<br/>빈 값으로 요청 시 모든 프로젝트 인원 참여로 간주", example = "[\"20241121\", \"20251234\"]", type = "array", implementation = String.class)
    private List<String> participants;

    public ScheduleCreateReqDto(Schedule schedule) {
        this.projId = schedule.getProjId().getProjId();
        this.date = schedule.getDate();
        this.scheName = schedule.getScheName();
        this.place = schedule.getPlace();
        this.category = schedule.getCategory();
        this.detail = schedule.getDetail();
    }
}
