package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RecordRequestDto {
    @Schema(description = "텍스트로 변환할 음성녹음 파일")
    private MultipartFile record;
}
