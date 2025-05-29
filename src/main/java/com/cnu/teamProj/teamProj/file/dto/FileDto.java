package com.cnu.teamProj.teamProj.file.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private String url;
    private String filename;
    private LocalDateTime uploadDate;

    public FileDto(String url, String filename) {
        this.url = url;
        this.filename = filename;
    }
}
