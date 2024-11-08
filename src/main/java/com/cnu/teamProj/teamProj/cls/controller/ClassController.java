package com.cnu.teamProj.teamProj.cls.controller;

import com.cnu.teamProj.teamProj.cls.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.cls.service.ClassService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/teamProj/class")
@Tag(name = "수업 관리", description = "수업 관리와 관련된 코드")
public class ClassController {

    private ClassService classService;
    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

//    @Operation(summary = "새로운 클래스 등록", description = "새로운 수업 코드를 등록합니다")
//    @Parameters({
//            @Parameter(name="classId", description = "과목코드", example = "CSE0000-01"),
//            @Parameter(name="className", description = "과목명", example = "java언어"),
//            @Parameter(name="professor", description = "교수명", example = "홍길동")
//    })
    @PostMapping("/enroll")
    public ResponseEntity<Boolean> enrollClass(@RequestBody ClassInfoDto dto) {
        Boolean result = classService.enrollClass(dto);
        return ResponseEntity.ok(result);
    }
}
