package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class ScheduleUploadReqDto {
    @Schema(
            description = "JSON 형식의 문자열 데이터",
            example = "{\n  \"participants\": [\n    {\n      \"name\": \"안혜연\",\n      \"id\": \"11111111\"\n    }\n  ],\n   \"sttContents\": \"string\",\n  \"date\": \"2025-05-27T14:30:00\",\n  \"fix\": \"5월 6일(월): UI 개선 시안 검토 회의 / 5월 8일(수): QA 팀의 버그 테스트 결과 공유 / 5월 13일(월): 다음 정기 회의 (기능 확정 및 개발 착수)\",\n  \"projId\": \"cse00001\",\n  \"title\": \"과제 제출 관련 회의\",\n  \"contents\": \"예시입니다\",\n  \"file\": \"string\"\n}"
    )
    private String param;

    @Schema(description = "녹음본 첨부파일", nullable = true)
    private MultipartFile file;
}
