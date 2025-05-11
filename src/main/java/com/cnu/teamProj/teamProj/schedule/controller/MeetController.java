package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.schedule.dto.MakePlanDto;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingLogDto;
import com.cnu.teamProj.teamProj.schedule.service.MakePlanService;
import com.cnu.teamProj.teamProj.schedule.service.MeetingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping(value ="/schedule/meeting", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
@Slf4j
public class MeetController {
    private final MakePlanService makePlanService;
    private final MeetingService meetingService;

    //test test test 제발
    @PostMapping("/adjust/upload")
    public ResponseEntity<String> inputWhen2Meet(@RequestBody MakePlanDto param) {
        int flag = makePlanService.uploadWhen2Meet(param);
        if(flag == 1) return new ResponseEntity<>("값이 성공적으로 등록되었습니다", HttpStatus.OK);
        else if(flag==0) return new ResponseEntity<>("존재하지 않는 유저 혹은 프로젝트입니다", HttpStatus.BAD_REQUEST);
        else if(flag==-2) return new ResponseEntity<>("시작 시간이 종료 시간보다 빠릅니다", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("값을 저장하는 과정에서 문제가 발생했습니다", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/view/log")
    public ResponseEntity<Map<String, Object>> getMeetingLog(@RequestParam Map<String, String> param) {
        return meetingService.getMeetingLog(param);
    }

    @PostMapping("/upload/log")
    public ResponseEntity<String> uploadMeetingLog(@RequestBody MeetingLogDto param) {
        return meetingService.updateMeetingLog(param);
    }

    @PostMapping("/convert-speech")
    public ResponseEntity<String> convertSpeechToText(@RequestPart("file")MultipartFile file) {
        return meetingService.convertSpeechToText(file);
    }
}
