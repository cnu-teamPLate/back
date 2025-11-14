package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.security.dto.SimpleUserInfoDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회의록 수정 시 요청 값")
public class MeetingLogReqDto {
    @Schema(description = "수정할 회의록의 회의 스케줄 아이디", example = "cse00001_3")
    private String scheId;
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
    @Schema(description = "녹음파일에서 추출한 텍스트 데이터", nullable = true)
    private String sttContents;
}
