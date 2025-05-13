package com.cnu.teamProj.teamProj.proj.controller;

import com.cnu.teamProj.teamProj.proj.dto.ProjCreateDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjUpdateDto;
import com.cnu.teamProj.teamProj.proj.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/projects", produces = "application/json; charset=utf8")
@Tag(name = "ProjectController", description = "프로젝트 관리와 관련된 API")
public class ProjectController {
    ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/view")
    @Operation(summary="유저가 참여중인 프로젝트 리스트", description = "userId:\"20241121\"를 key:value쌍으로 전달해주면 됨.")
    @Parameter(name = "userId", example = "20211079")
    public List<ProjDto> getProjs(@RequestParam(value = "userId") String userId) {
        return projectService.findProjectsByUserId(userId);
    }

    @PostMapping("/create")
    @Operation(summary = "프로젝트 생성")
    @Parameters(
            {
                    @Parameter(name = "date", description = "프로젝트 마감 날짜", example = "2025-01-15T00:02:27.000Z"),
                    @Parameter(name="goal", description = "목표", example = "A+"),
                    @Parameter(name="projName", description = "프로젝트 이름", example = "최강"),
                    @Parameter(name = "github", description = "깃헙 주소", example = "https://github.com"),
                    @Parameter(name = "classId", description = "수업 코드", example = "1")
            }
    )
    public ResponseEntity<String> createProjs(@RequestBody ProjCreateDto dto) {
        int flag = projectService.createProject(dto);
        if(flag == -1) return new ResponseEntity<>("등록되지 않은 수업입니다", HttpStatus.BAD_REQUEST);
        if(flag == 1) return new ResponseEntity<>("정상적으로 등록되었습니다", HttpStatus.OK);
        if(flag == -2) return new ResponseEntity<>("날짜 형식이 잘못되었습니다", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>("예상치 못한 오류가 발생했습니다",HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update/{projectId}")
    @Operation(summary="프로젝트 정보 업데이트", description="업데이트할 프로젝트 정보와 ID를 전달하면 됩니다.")
    @Parameters({
            @Parameter(name = "teamName", description = "팀 이름", example = "Updated"),
            @Parameter(name = "date", description = "프로젝트 마감일", example = "2025-01-15T00:02:27.000Z"),
            @Parameter(name = "goal", description = "목표", example = "A-"),
            @Parameter(name = "projName", description = "프로젝트 명", example = "일석이조"),
            @Parameter(name = "github", description = "깃헙 주소", example = "https://github.com"),
            @Parameter(name = "classId", description = "수업 코드", example = "1")
    })
    public ResponseEntity<String> updateProjs(@PathVariable("projectId") String projectId, @RequestBody ProjUpdateDto dto) {
        dto.setProjectId(projectId);
        boolean isUpdated=projectService.updateProject(dto);
        if (isUpdated) {
            return new ResponseEntity<>("프로젝트가 성공적으로 업데이트되었습니다", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("업데이트에 실패했습니다. 잘못된 프로젝트 ID일 수 있습니다.", HttpStatus.BAD_REQUEST);
        }
    }

}
