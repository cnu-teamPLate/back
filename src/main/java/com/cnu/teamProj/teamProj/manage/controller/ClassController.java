package com.cnu.teamProj.teamProj.manage.controller;

import com.cnu.teamProj.teamProj.manage.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.manage.service.ClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping(value = "/teamProj/class", produces = "application/json; charset = utf-8")
@Tag(name = "ClassController", description = "수업 관리와 관련된 API")
public class ClassController {
//test2
    private ClassService classService;
    @Autowired
    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @Operation(summary = "새로운 클래스 등록", description = "새로운 수업 코드를 등록합니다")
    @Parameters(value = {@Parameter(name = "classId", description = "15자 이내의 수업 코드", example = "cse2222"),
                        @Parameter(name = "className", description = "100자 이내의 수업명", example = "JAVA 언어"),
                        @Parameter(name = "professor", description = "교수님 성함", example = "홍길동")})
    @ApiResponses(value = {@ApiResponse(responseCode = "200 OK", description = "등록이 완료되었습니다"),
                        @ApiResponse(responseCode = "400 BAD_REQUEST", description = "이미 등록된 수업입니다"),
                        @ApiResponse(responseCode = "405 METHOD_NOT_ALLOWED", description = "예상치 못한 문제가 발생했습니다")})
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollClass(@RequestBody ClassInfoDto dto) {
        int result = classService.enrollClass(dto);
        if(result == 0) return new ResponseEntity<>("이미 등록된 수업입니다", HttpStatus.BAD_REQUEST);
        if(result == 1) return new ResponseEntity<>("등록이 완료되었습니다", HttpStatus.OK);;
        return new ResponseEntity<>("예상치 못한 문제가 발생했습니다.", HttpStatus.METHOD_NOT_ALLOWED);
    }




    @GetMapping("/id/{classId}")
    @Operation(summary = "클래스 ID로 클래스 정보 조회", description = "클래스 ID로 해당 클래스 정보를 반환합니다")
    @Parameter(name = "classId", description = "클래스 ID", example = "cse2222")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "클래스 정보 반환 성공"),
            @ApiResponse(responseCode = "404", description = "해당 클래스가 존재하지 않음")
    })
    public ResponseEntity<ClassInfoDto> getClassById(@PathVariable(name = "classId") String classId) {
        ClassInfoDto result = classService.findByClassId(classId);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/name/{className}")
    @Operation(summary = "클래스 이름으로 클래스 정보 조회", description = "클래스 이름으로 해당 클래스 정보를 반환합니다")
    @Parameter(name = "className", description = "클래스 이름", example = "JAVA 언어")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "클래스 정보 반환 성공"),
            @ApiResponse(responseCode = "404", description = "해당 클래스가 존재하지 않음")
    })
    public ResponseEntity<ClassInfoDto> getClassByName(@PathVariable(name = "className") String className) {
        ClassInfoDto result = classService.findByClassName(className);
        if (result == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
