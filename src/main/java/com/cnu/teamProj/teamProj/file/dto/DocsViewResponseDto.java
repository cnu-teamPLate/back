package com.cnu.teamProj.teamProj.file.dto;

import com.cnu.teamProj.teamProj.file.entity.Docs;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class DocsViewResponseDto {
    private int fileId;
    private String userName;
    private String projName;
    private String userId;
    private String projId;
    private String url;
    private String title;
    private String detail;
    private LocalDateTime uploadDate;
    private int category;
    private String filename;
    private String taskName;

    //유저 이름과 프로젝트 이름을 제외한 값으로 생성
    public DocsViewResponseDto(Docs docs) {
        this.fileId = docs.getFileId();
        this.url = docs.getUrl();
        this.title = docs.getTitle();
        this.detail = docs.getDetail();
        this.uploadDate = docs.getUploadDate();
        this.category = docs.getCategory();
        this.filename = docs.getFilename();
        this.userId = docs.getId();
        this.projId = docs.getProjId();
    }
}
