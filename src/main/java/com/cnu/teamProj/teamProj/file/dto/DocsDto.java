package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class DocsDto {
    @Schema(description = "학번", example = "20211079")
    private String id;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "문서 이름", example = "마케팅 보고서")
    private String title;
    @Schema(description = "문서에 대한 설명", example = "바나나 우유 마케팅 조사한 내용입니다")
    private String detail;
    @Schema(description = "특정 과제에 종속된 파일인 경우 -> 과제의 아이디<br/>문서와 관련된 파일인 경우 -> -1", defaultValue = "-1")
    private int category;
    @Schema(description = "파일 데이터가 아닌 외부 url을 저장할 경우", nullable = true)
    private List<String> url;

    public DocsDto(DocsUploadRequestDto uploadRequestDto) {
        this.id = uploadRequestDto.getId();
        this.projId = uploadRequestDto.getProjId();
        this.title = uploadRequestDto.getTitle();
        this.detail = uploadRequestDto.getDetail();
        this.category = uploadRequestDto.getCategory();
        this.url = uploadRequestDto.getUrl();
    }
}
