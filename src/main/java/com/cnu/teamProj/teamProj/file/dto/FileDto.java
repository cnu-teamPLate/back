package com.cnu.teamProj.teamProj.file.dto;

import com.cnu.teamProj.teamProj.file.entity.File;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    @Schema(description = "파일 아이디", nullable = true)
    private int id;
    @Schema(description = "파일 위치")
    private String url;
    @Schema(description = "파일 명<br/>만약 외부 url 이라면 filename은 null값임")
    private String filename;
    @Schema(description = "파일 업로드 날짜", example = "2025-05-25T14:30:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime uploadDate;

    public FileDto(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }

    public FileDto(File file) {
        this.id = file.getFileId();
        this.url = file.getUrl();
        this.filename = file.getFilename();
        this.uploadDate = file.getUploadDate();
    }
}
