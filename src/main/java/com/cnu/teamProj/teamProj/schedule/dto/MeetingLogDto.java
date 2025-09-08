package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.security.dto.SimpleUserInfoDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
/*
*
* @Parameters({
            @Parameter(name = "scheId", example = "cse00001_3"),
            @Parameter(name = "projId", example = "cse00001"),
            @Parameter(name = "contents", example = "예시입니다", description = "회의 내용"),
            @Parameter(name = "fix", example = "5월 6일(월): UI 개선 시안 검토 회의 / 5월 8일(수): QA 팀의 버그 테스트 결과 공유 / 5월 13일(월): 다음 정기 회의 (기능 확정 및 개발 착수)", description = "회의 후 확정된 내용")
    })
* */
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "자세한 회의록 정보")
public class MeetingLogDto {
    @Schema(description = "스케줄 아이디", example = "cse00001_3")
    private String scheId;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "회의록 내용", example = "예시입니다")
    private String contents;
    @Schema(description = "회의록 제목", example = "과제 제출 관련 회의")
    private String title;
    @Schema(description = "회의 날짜", example = "2025-05-27T14:30:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    @Schema(description = "확정된 내용", example = "5월 6일(월): UI 개선 시안 검토 회의 / 5월 8일(수): QA 팀의 버그 테스트 결과 공유 / 5월 13일(월): 다음 정기 회의 (기능 확정 및 개발 착수)")
    private String fix;
    @Schema(description = "참여자 목록")
    private List<SimpleUserInfoDto> participants;
    @Schema(description = "녹음본", nullable = true)
    private String fileUrl;
    @Schema(description = "녹음파일에서 추출한 텍스트 데이터", nullable = true)
    private String sttContents;

    public MeetingLogDto(MeetingLog entity) {
        this.scheId = entity.getScheId().getScheId();
        this.projId = entity.getProjId().getProjId();
        this.contents = entity.getContents();
        this.fix = entity.getFix();
        this.sttContents = getSttContents();
    }
}
