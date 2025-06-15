package com.cnu.teamProj.teamProj.proj.controller;

import com.cnu.teamProj.teamProj.proj.dto.ProjCreateDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjUpdateDto;
import com.cnu.teamProj.teamProj.proj.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다<br/>단, 존재하지 않는 학번이 들어올 경우 저장에 실패한 학번 정보가 같이 반환됨"),
            @ApiResponse(responseCode = "400", description = "응답에 필요한 필수 요청 값이 전달되지 않았습니다"),
            @ApiResponse(responseCode = "404", description = "응답에 필요한 필수 요청 값이 전달되지 않았습니다")
    })
    public ResponseEntity<?> createProjs(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody ProjCreateDto dto) {
        return projectService.createProject(dto);
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
