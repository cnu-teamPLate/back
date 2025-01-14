package com.cnu.teamProj.teamProj.manage.controller;

import com.cnu.teamProj.teamProj.manage.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.manage.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/teamProj/class", produces = "application/json; charset = utf-8")
@Tag(name = "수업 관리", description = "수업 관리와 관련된 코드")
public class ClassController {

    private ClassService classService;
    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @Operation(summary = "새로운 클래스 등록", description = "새로운 수업 코드를 등록합니다")
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollClass(@RequestBody ClassInfoDto dto) {
        int result = classService.enrollClass(dto);
        if(result == 0) return new ResponseEntity<>("이미 등록된 수업입니다", HttpStatus.BAD_REQUEST);
        if(result == 1) return new ResponseEntity<>("등록이 완료되었습니다", HttpStatus.OK);;
        return new ResponseEntity<>("예상치 못한 문제가 발생했습니다.", HttpStatus.BAD_REQUEST);
    }
}
