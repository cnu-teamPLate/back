package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class When2meetListDto extends When2meetRequestDto{
    @Schema(description = "웬투밋 폼 ID")
    private int formId;

    public When2meetListDto(When2meet when2meet, List<TimeRangeDto> dates, int formId) {
        super(when2meet, dates);
        this.formId = formId;
    }
}
