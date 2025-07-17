package com.cnu.teamProj.teamProj.task.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.task.dto.DetailTaskDto;
import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/task", produces = "application/json; charset=utf8") // 추가됨
@RequiredArgsConstructor
@Tag(name="TaskController", description = "과제 관리와 관련된 컨트롤러")
public class TaskController {
    private final TaskService taskService;
    private final DocsService docsService;

    @PostMapping("/post")
    public ResponseEntity<?> createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO);
    }

    @Operation(summary = "유저에게 할당된 과제를 불러오는 api")
    @Parameters(value = {
            @Parameter(name = "projId", description = "프로젝트 아이디", example = "cse00001"),
            @Parameter(name = "id", description = "유저 아이디", example = "20241121")
    })
    @GetMapping("/view")
    public ResponseEntity<?> getTasks(@RequestParam(value = "projId", required = false) String projId,
                                      @RequestParam(value = "id", required = false) String id) {

        List<TaskDTO> tasks = taskService.getTasksByFlexibleParams(projId, id);

        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("조회된 데이터가 없습니다.");
        }


        return ResponseEntity.ok(tasks);
    }

    @PutMapping("{taskId}")
    @Operation()
    public ResponseEntity<String> completeTask(@PathVariable Integer taskId) {
        boolean updated=taskService.completeTask(taskId);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 taskId가 존재하지 않습니다.");

        }
        return ResponseEntity.ok("과제가 완료되었습니다.");
    }

    //특정 과제 아이디에 대한 디테일한 정보 가져오기
    @GetMapping("/view-detail/{taskId}")
    @Operation(summary = "특정 과제에 대한 디테일 정보 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "해당 아이디의 값이 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "더 이상 존재하지 않는 유저 혹은 프로젝트의 과제 입니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "과제의 디테일한 정보", content = @Content(schema = @Schema(implementation = DetailTaskDto.class)))
    })
    public ResponseEntity<?> getDetailTaskByTaskId(@PathVariable(value = "taskId") String taskId) {
        return taskService.getTaskByTaskId(taskId);
    }

}

