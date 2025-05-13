package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.schedule.dto.CalendarScheduleDto;
import com.cnu.teamProj.teamProj.schedule.dto.ScheduleDto;
import com.cnu.teamProj.teamProj.schedule.dto.ScheduleUpdateDto;
import com.cnu.teamProj.teamProj.schedule.dto.TaskUpdateDto;
import com.cnu.teamProj.teamProj.schedule.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
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
    @Parameters({
            @Parameter(name = "projId", example = "cse00001"),
            @Parameter(name = "date", example = "2025-01-01T00:02:27.Z", description = "기준일로, 해당일로부터 일주일에 해당하는 데이터를 불러옴"),
            @Parameter(name = "cate", example = "meeting", description = "회의 = meeting<br/>과제 = task<br/>일정 = plan")
    })
    public ResponseEntity<Object> viewWeekly(@RequestBody Map<String, Object> param) {
        param.put("term", "w");
        return scheduleService.getObjectResponseEntity(param);
    }



    @GetMapping("/monthly")
    @Operation(summary = "월간 일정 불러오기 api")
    @Parameters({
            @Parameter(name = "projId", example = "cse00001"),
            @Parameter(name = "date", example = "2025-01-01T00:02:27.Z", description = "기준일로, 해당일로부터 한 달에 해당하는 데이터를 불러옴"),
            @Parameter(name = "cate", example = "meeting", description = "회의 = meeting<br/>과제 = task<br/>일정 = plan")
    })
    public ResponseEntity<Object> viewMonthly(@RequestBody Map<String, Object> param) {
        param.put("term", "m");
        return scheduleService.getObjectResponseEntity(param);
    }


    @PostMapping("/upload")
    @Operation(summary = "스케줄 업로드 api")
    @Parameters({
            @Parameter(name = "projId", example = "cse00001"),
            @Parameter(name = "date", example = "2025-01-01T00:02:27.Z", description = "날짜 지정"),
            @Parameter(name = "scheName", example = "과제 제출", description = "스케줄 이름"),
            @Parameter(name = "place", example = "온라인", description = "약속 장소"),
            @Parameter(name = "category", example = "meeting", description = "회의 = meeting<br/>과제 = task<br/>일정 = plan"),
            @Parameter(name = "detail", example = "발표 파일, 발표자 리스트 제출해야 함", description = "스케줄 관련 설명"),
            @Parameter(name = "participants", example = "[\"00000000\", \"01111111\"]", description = "참여자의 학번을 배열로 넘겨야 함")
    })
    public ResponseEntity<String> uploadSchedule(@RequestBody ScheduleDto dto) {
        int ret = scheduleService.createSchedule(dto);
        if(ret == 0) return new ResponseEntity<>("입력값이 null 입니다", HttpStatus.BAD_REQUEST);
        if(ret == -1) return new ResponseEntity<>("존재하지 않는 프로젝트입니다", HttpStatus.NOT_FOUND);
        if(ret == -2) return new ResponseEntity<>("존재하지 않는 유저가 참여자 목록에 포홤되었습니다", HttpStatus.NOT_FOUND);
        if(ret == 1) return new ResponseEntity<>("정상적으로 저장되었습니다", HttpStatus.OK);
        else return new ResponseEntity<>("예상치못한 오류가 발생했습니다", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/delete/schedule")
    @Operation(summary = "스케줄 삭제")
    @Parameter(name = "scheId", example = "cse00001_1")
    public ResponseEntity<String> deleteSchedule(@RequestBody Map<String, Object> param) {
        int ret;
        if(param.containsKey("taskId")) {
            ret = scheduleService.deleteTask((Integer)param.get("taskId"));
        } else if (param.containsKey("scheId")) {
            ret = scheduleService.deleteSchedule(param.get("scheId").toString());
        } else ret = 0;

        if(ret == 1) return new ResponseEntity<>("성공적으로 삭제되었습니다", HttpStatus.OK);
        else if(ret == -1) return new ResponseEntity<>("존재하는 일정이 없습니다", HttpStatus.NOT_FOUND);
        else return new ResponseEntity<>("예상치 못한 문제가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PutMapping("/update/team-schedule")
    @Operation(summary = "팀 스케줄 수정")
    public ResponseEntity<String> updateSchedule(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "스케줄 수정 시 요청 값") @RequestBody ScheduleUpdateDto dto) {
        return scheduleService.updateSchedule(dto);
    }

    @PutMapping("/update/task")
    public ResponseEntity<String> updateTask(@RequestBody TaskUpdateDto dto) {
        int ret = scheduleService.updateTask(dto);
        if(ret==1) return new ResponseEntity<>("성공적으로 업데이트 되었습니다", HttpStatus.OK);
        else return new ResponseEntity<>("해당 스케줄 아이디에 속하는 레코드 값이 없습니다", HttpStatus.OK);
    }
}
