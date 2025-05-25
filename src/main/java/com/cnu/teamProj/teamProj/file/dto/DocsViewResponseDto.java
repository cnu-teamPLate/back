package com.cnu.teamProj.teamProj.file.dto;

import com.cnu.teamProj.teamProj.file.entity.Docs;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class DocsViewResponseDto {
    @Schema(description = "파일 아이디", example = "4")
    private int fileId;
    @Schema(description = "학번", example = "홍길동")
    private String userName;
    @Schema(description = "프로젝트명", example = "teamPlate")
    private String projName;
    @Schema(description = "학번", example = "01111111")
    private String userId;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "문서 url", example = "https://teamplate-bucket-0.s3.ap-northeast-2.amazonaws.com/cse00001/푸우1.png")
    private String url;
    @Schema(description = "문서 제목", example = "마케팅 보고서")
    private String title;
    @Schema(description = "자세한 설명", example = "마케팅 보고서 입니다.")
    private String detail;
    @Schema(description = "업로드 날짜 : [연, 월, 일, 시, 분, 초]", example = "[2025, 1, 14, 0, 2, 27]", type = "array")
    private LocalDateTime uploadDate;
    @Schema(description = "-1이 아니면 과제와 관련된 파일임을 의미함", example = "-1")
    private int category;
    @Schema(description = "파일명, 문서가 파일 형태가 아니라면 null 값", example = "푸우1.png", nullable = true)
    private String filename;
    @Schema(description = "과제와 관련된 파일일 경우 과제명", example = "개발 환경 세팅", nullable = true)
    private String taskName;

    //유저 이름과 프로젝트 이름을 제외한 값으로 생성
    public DocsViewResponseDto(Docs docs) {
        this.fileId = docs.getFileId();
        this.url = docs.getUrl();
        this.title = docs.getTitle();
        this.detail = docs.getDetail();
        this.uploadDate = docs.getUploadDate();
        this.category = docs.getCategory();
        this.filename = docs.getFilename();
        this.userId = docs.getId();
        this.projId = docs.getProjId();
    }
}
