package com.cnu.teamProj.teamProj.cls.controller;

import com.cnu.teamProj.teamProj.cls.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.cls.service.ClassService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teamProj/class")
@ApiOperation(value = "강의 관리", notes = "수업 등록과 관련된 코드")
public class ClassController {

    private ClassService classService;
    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<Boolean> enrollClass(@RequestBody ClassInfoDto dto) {
        Boolean result = classService.enrollClass(dto);
        return ResponseEntity.ok(result);
    }
}
