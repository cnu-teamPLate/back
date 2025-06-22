package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.schedule.dto.*;
import com.cnu.teamProj.teamProj.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/schedule/check", produces = "application/json; charset=utf8")
@Tag(name = "ScheduleController", description = "일정 관리와 관련된 api 입니다")
public class ScheduleController {

    ScheduleService scheduleService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    //유저 아이디, 날짜 정보
    /**
     * @param param
     *  - id : 유저 아이디
     *  - projId : 프로젝트 아이디
     *  - date : 날짜 정보
     *  - term(기한설정) : w(=주) or m(=달) - 프론트에서 전달해주지 않아도 됨
     *  - category : 분류 - 회의/과제/일정/
     *      - category 값이 없으면 전체 스케줄을 반환
     *      - 일정과 회의는 같은 테이블로 묶임
     * */
    @GetMapping("/weekly")
    @Operation(summary = "주간 일정 불러오기 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content=@Content(schema = @Schema(implementation = CalendarScheduleDto.class))),
            @ApiResponse(responseCode = "404", content=@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "존재하지 않는 프로젝트", value = "{\"message\": \"파일의 이름명이 잘못되었습니다\", \"code\": 404}"),
                            @ExampleObject(name = "존재하지 않는 유저", value = "{\"message\": \"존재하는 유저가 아닙니다\", \"code\": 404}")
                    }
            )),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "필수 요청 값이 없음", value = "{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다.\", \"code\": 400}")
                    }
            ))
    })
    public ResponseEntity<?> viewWeekly(@ParameterObject ScheduleViewReqDto param) {
        return scheduleService.getSchedule(param, "w");
    }



    @GetMapping("/monthly")
    @Operation(summary = "월간 일정 불러오기 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content=@Content(schema = @Schema(implementation = CalendarScheduleDto.class))),
            @ApiResponse(responseCode = "404", content=@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "존재하지 않는 프로젝트", value = "{\"message\": \"파일의 이름명이 잘못되었습니다\", \"code\": 404}"),
                            @ExampleObject(name = "존재하지 않는 유저", value = "{\"message\": \"존재하는 유저가 아닙니다\", \"code\": 404}")
                    }
            )),
            @ApiResponse(responseCode = "400", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "필수 요청 값이 없음", value = "{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다.\", \"code\": 400}"),
                            @ExampleObject(name = "날짜 형식이 잘못됨", value = "{\"message\": \"요청 형식이 잘못되었습니다\", \"code\": 400}")
                    }
            ))
    })
    public ResponseEntity<?> viewMonthly(@ParameterObject ScheduleViewReqDto dto) {
        return scheduleService.getSchedule(dto, "m");
    }


    @PostMapping("/upload")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content=@Content(schema = @Schema(implementation = ResponseDto.class), examples = {@ExampleObject(name = "요청 성공", value="{\"message\": \"요청이 성공적으로 처리되었습니다\", \"code\": 200}")})),
            @ApiResponse(responseCode = "400", content=@Content(schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject(name = "필수 파라미터 없음", value="{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다.\", \"code\": 400}"), @ExampleObject(name = "날짜 형식이 잘못됨", value = "{\"message\": \"요청 형식이 잘못되었습니다\", \"code\": 400}")})),
            @ApiResponse(responseCode = "404", content=@Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                        @ExampleObject(name = "없는 프로젝트", value="{\"message\": \"존재하는 프로젝트 아이디가 아닙니다\", \"code\": 404}"),
                        @ExampleObject(name = "없는 유저", value="{\"message\": \"존재하지 않는 유저 아이디입니다\", \"code\": 404}")}))
    })
    @Operation(summary = "스케줄 업로드 api", description = "⚠️participants 값은 [\"20241121\",\"20251234\"] 형태로 보내줘야 함!")
    public ResponseEntity<?> uploadSchedule(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody ScheduleCreateReqDto dto) {
        return scheduleService.createSchedule(dto);
    }

    @DeleteMapping("/delete/schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = {@ExampleObject(name = "존재하지 않는 스케줄", value = "{\"message\": \"존재하지 않는 스케줄 아이디입니다\", \"code\": 404}")})),
            @ApiResponse(responseCode = "200", content=@Content(schema = @Schema(implementation = ResponseDto.class), examples = {@ExampleObject(name = "요청 성공", value="{\"message\": \"요청이 성공적으로 처리되었습니다\", \"code\": 200}")}))
    })
    @Operation(summary = "스케줄 삭제", description = "⚠️scheId 값은 [\"CSE00011_4\",\"CSE00011_3\"] 형태로 보내줘야 함!")
    public ResponseEntity<?> deleteSchedule(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody ScheduleDeleteReqDto dto) {
        return scheduleService.deleteSchedule(dto);
    }

    @PutMapping("/update/team-schedule")
    @Operation(summary = "팀 스케줄 수정", description = "⚠️participants 값은 [\"20241121\",\"20251234\"] 형태로 보내줘야 함!")
    @ApiResponses(value = {
           @ApiResponse(responseCode = "404", content = @Content(
                   mediaType = "application/json",
                   schema=@Schema(implementation = ErrorResponse.class),
                   examples = {
                           @ExampleObject(name = "없는 스케줄 아이디", value = "{\"message\": \"해당 아이디의 스케줄 레코드가 존재하지 않습니다\", \"code\": 404}"),
                           @ExampleObject(name = "없는 프로젝트 아이디", value = "{\"message\": \"존재하는 프로젝트가 아닙니다\", \"code\": 404}")
                   }
           )),
            @ApiResponse(responseCode = "400", content=@Content(mediaType = "application/json", schema=@Schema(implementation = ErrorResponse.class), examples = {@ExampleObject(name="잘못된 유저 값", value = "{\"message\": \"프로젝트 구성원이 아닌 유저는 참여자로 추가할 수 없습니다\", \"code\": 400}")}))
    })
    public ResponseEntity<?> updateSchedule(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "스케줄 수정 시 요청 값") @RequestBody ScheduleUpdateReqDto dto) {
        return scheduleService.updateSchedule(dto);
    }

    @PutMapping("/update/task")
    public ResponseEntity<String> updateTask(@RequestBody TaskUpdateDto dto) {
        int ret = scheduleService.updateTask(dto);
        if(ret==1) return new ResponseEntity<>("성공적으로 업데이트 되었습니다", HttpStatus.OK);
        else return new ResponseEntity<>("해당 스케줄 아이디에 속하는 레코드 값이 없습니다", HttpStatus.OK);
    }
}
