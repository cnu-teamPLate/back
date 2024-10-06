package com.cnu.teamProj.teamProj.cls.controller;

import com.cnu.teamProj.teamProj.cls.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.cls.service.ClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teamProj/class")
@Api(value = "강의와 관련된 url path")
public class ClassController {

    private ClassService classService;
    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @ApiOperation(value = "새로운 클래스 등록", notes = "새로운 수업 코드를 등록합니다", response = ResponseEntity.class, tags = {"Class API"}, httpMethod = "POST")
    @PostMapping("/enroll")
    public ResponseEntity<Boolean> enrollClass(@RequestBody ClassInfoDto dto) {
        Boolean result = classService.enrollClass(dto);
        return ResponseEntity.ok(result);
    }
}
