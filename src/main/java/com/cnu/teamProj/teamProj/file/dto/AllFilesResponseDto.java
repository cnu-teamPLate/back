package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AllFilesResponseDto {
    @Schema(description = "파일별 삭제 결과 목록")
    private List<FileResponseDto> results;
}
