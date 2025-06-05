package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class FileDeleteRequestDto {
    @Schema(description = "삭제하고자 하는 문서 아이디 리스트")
    private List<Integer> docs;
}
