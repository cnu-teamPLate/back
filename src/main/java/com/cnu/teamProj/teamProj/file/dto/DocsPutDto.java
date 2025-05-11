package com.cnu.teamProj.teamProj.file.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class DocsPutDto {
    private int fileId;
    private String title;
    private String detail;
    @Nullable
    private String url;
}
