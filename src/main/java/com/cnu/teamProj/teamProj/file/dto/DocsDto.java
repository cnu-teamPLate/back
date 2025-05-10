package com.cnu.teamProj.teamProj.file.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;

@Data
public class DocsDto {
    private String id;
    private String projId;
    private String title;
    private String detail;
    private int category;
    private String url;
}
