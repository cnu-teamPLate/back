package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class When2meetDetailRequestDto {
    @Schema(description = "웬투밋 폼 아이디", example = "1", type = "int")
    private int when2meetId;
    @ArraySchema(
            arraySchema = @Schema(description = "각 유저가 선택한 값 리스트"),
            schema = @Schema(implementation = When2meetDetailDto.class)
    )
    private List<When2meetDetailDto> details;
}
