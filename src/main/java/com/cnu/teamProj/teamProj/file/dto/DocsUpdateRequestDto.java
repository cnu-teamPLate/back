package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class DocsUpdateRequestDto {
    @Schema(description = "파일 아이디", example = "10")
    private int fileId;
    @Schema(description = "문서 제목", example = "핀토스 1차 프로젝트")
    private String title;
    @Schema(description = "문서에 대한 설명", example = "유저 프로그램 개발하기")
    private String detail;
    @Schema(description = "참고 파일 url")
    @Nullable
    private String url;
    @Schema(description = "업로드 하고자 하는 파일")
    private MultipartFile file;
}
