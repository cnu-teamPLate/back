package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileResponseDto {
    @Schema(description = "파일 아이디", example = "14")
    private int fileId;
    @Schema(description = "처리 결과", example = "success 혹은 fail:원인")
    private String result;

    public FileResponseDto(int fileId, String cause){
        this.fileId = fileId;
        if(cause == null) {
            this.result = "success";
        } else {
            this.result = String.format("fail:%s",cause);
        }
    }
}
