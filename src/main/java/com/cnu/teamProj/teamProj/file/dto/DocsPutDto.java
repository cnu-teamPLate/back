package com.cnu.teamProj.teamProj.file.dto;

import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocsPutDto {
    private int fileId;
    private String title;
    private String detail;
    @Nullable
    private String url;

    public DocsPutDto(DocsUpdateRequestDto dto) {
        this.fileId = dto.getFileId();
        this.title = dto.getTitle();
        this.detail = dto.getDetail();
        this.url = dto.getUrl();
    }
}
