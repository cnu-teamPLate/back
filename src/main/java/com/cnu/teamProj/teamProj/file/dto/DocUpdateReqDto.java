package com.cnu.teamProj.teamProj.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@Setter
public class DocUpdateReqDto {
    @Schema(description = "수정하려고 하는 문서 아이디", example = "1")
    private int id;
    @Schema(description = "문서 제목", example = "마케팅 보고서")
    private String title;
    @Schema(description = "자세한 설명", example = "마케팅 보고서 입니다.")
    private String detail;
    @Schema(description = "-1이 아니면 과제와 관련된 파일임을 의미함", example = "-1")
    private int category;
    @Schema(description = "외부 url 리스트", nullable = true)
    private List<String> urls;
    @Schema(description = "파일 리스트", nullable = true)
    private List<MultipartFile> files;
}
