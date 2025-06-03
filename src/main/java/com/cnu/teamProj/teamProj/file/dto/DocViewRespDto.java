package com.cnu.teamProj.teamProj.file.dto;

import com.cnu.teamProj.teamProj.file.entity.Doc;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class DocViewRespDto {
    @Schema(description = "doc 아이디", example = "1")
    private int id;
    @Schema(description = "문서 제목", example = "마케팅 보고서")
    private String title;
    @Schema(description = "자세한 설명", example = "마케팅 보고서 입니다.")
    private String detail;
    @Schema(description = "업로드 날짜", example = "2025-05-27T14:30:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadDate;
    @Schema(description = "생성된 날짜", example = "2025-05-25T14:30:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDate;
    @Schema(description = "사용자 학번", example = "01111111")
    private String userId;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
    @Schema(description = "프로젝트명", example = "teamPlate")
    private String projName;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "-1이 아니면 과제와 관련된 파일임을 의미함", example = "-1")
    private int category;
    @Schema(description = "문서에 등록된 파일 정보 리스트")
    private List<FileDto> files;

    public DocViewRespDto(Doc doc) {
        this.id = doc.getId();
        this.title = doc.getTitle();
        this.detail = doc.getDetail();
        this.uploadDate = doc.getUploadDate();
        this.createdDate = doc.getCreatedDate();
        this.userId = doc.getCreatedBy().getId();
        this.userName = doc.getCreatedBy().getUsername();
        this.projId = doc.getProjId().getProjId();
        this.projName = doc.getProjId().getProjName();
        this.category = doc.getCategory();
    }
}
