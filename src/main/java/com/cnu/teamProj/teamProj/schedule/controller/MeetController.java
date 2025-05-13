package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.schedule.dto.MakePlanDto;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingLogDto;
import com.cnu.teamProj.teamProj.schedule.service.MakePlanService;
import com.cnu.teamProj.teamProj.schedule.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(value ="/schedule/meeting", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MeetController", description = "회의 및 시간 조정과 관련된 API")
public class MeetController {
    private final MakePlanService makePlanService;
    private final MeetingService meetingService;

    //test test test 제발
    @PostMapping("/adjust/upload")
    @Operation(summary = "웬투밋 생성")
    @Parameters({
            @Parameter(name = "userId", description = "학번", example = "20211122"),
            @Parameter(name = "projId", description = "프로젝트 아이디", example = "cse00001"),
            @Parameter(name = "start", description = "가능한 시작 시간", example = "2025-01-15T00:02:27.000Z"),
            @Parameter(name = "end", description = "가능한 종료 시간", example = "2025-01-14T00:02:27.000Z")
    })
    public ResponseEntity<String> inputWhen2Meet(@RequestBody MakePlanDto param) {
        int flag = makePlanService.uploadWhen2Meet(param);
        if(flag == 1) return new ResponseEntity<>("값이 성공적으로 등록되었습니다", HttpStatus.OK);
        else if(flag==0) return new ResponseEntity<>("존재하지 않는 유저 혹은 프로젝트입니다", HttpStatus.BAD_REQUEST);
        else if(flag==-2) return new ResponseEntity<>("시작 시간이 종료 시간보다 빠릅니다", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("값을 저장하는 과정에서 문제가 발생했습니다", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/view/log")
    @Operation(summary = "회의록 불러오기", description = "필요에 따라 스케줄 아이디 또는 프로젝트 아이디 또는 둘 다 담아 요청값으로 넘겨주면 됨.<br/>둘 중 하나는 있어야 함.")
    @Parameters({
            @Parameter(name = "scheId", description = "스케줄 레코드 아이디", example = "cse00001_2"),
            @Parameter(name = "projId", description = "프로젝트 아이디", example = "cse00001")
    })
    public ResponseEntity<Map<String, Object>> getMeetingLog(@RequestParam Map<String, String> param) {
        return meetingService.getMeetingLog(param);
    }

    @PostMapping("/upload/log")
    @Operation(summary = "회의록 올리기")
    @Parameters({
            @Parameter(name = "scheId", example = "cse00001_3"),
            @Parameter(name = "projId", example = "cse00001"),
            @Parameter(name = "contents", example = "예시입니다", description = "회의 내용"),
            @Parameter(name = "fix", example = "5월 6일(월): UI 개선 시안 검토 회의 / 5월 8일(수): QA 팀의 버그 테스트 결과 공유 / 5월 13일(월): 다음 정기 회의 (기능 확정 및 개발 착수)", description = "회의 후 확정된 내용")
    })
    public ResponseEntity<String> uploadMeetingLog(@RequestBody MeetingLogDto param) {
        return meetingService.updateMeetingLog(param);
    }

    @PostMapping("/convert-speech")
    @Operation(summary = "녹음파일을 텍스트로 변환해주는 api")
    @Parameter(name = "file", description = "MultipartFile 형태의 변수로 보내줘야 합니다")
    public ResponseEntity<String> convertSpeechToText(@RequestPart("file")MultipartFile file) {
        return meetingService.convertSpeechToText(file);
    }
}
