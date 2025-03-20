package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.schedule.dto.MakePlanDto;
import com.cnu.teamProj.teamProj.schedule.service.MakePlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value ="/schedule/adjust", produces = "application/json; charset=utf8")
public class WhenToMeetController {
    MakePlanService makePlanService;

    public WhenToMeetController(MakePlanService makePlanService) {
        this.makePlanService = makePlanService;
    }
    //test
    @PostMapping("/upload")
    public ResponseEntity<String> inputWhen2Meet(@RequestBody MakePlanDto param) {
        int flag = makePlanService.uploadWhen2Meet(param);
        if(flag == 1) return new ResponseEntity<>("값이 성공적으로 등록되었습니다", HttpStatus.OK);
        else if(flag==0) return new ResponseEntity<>("존재하지 않는 유저 혹은 프로젝트입니다", HttpStatus.BAD_REQUEST);
        else if(flag==-2) return new ResponseEntity<>("시작 시간이 종료 시간보다 빠릅니다", HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>("값을 저장하는 과정에서 문제가 발생했습니다", HttpStatus.BAD_REQUEST);
    }
}
