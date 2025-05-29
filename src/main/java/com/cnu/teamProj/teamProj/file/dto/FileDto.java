package com.cnu.teamProj.teamProj.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileDto {
    private String url;
    private String filename;
    private LocalDateTime uploadDate;

    public FileDto(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }
}
