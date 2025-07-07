package com.cnu.teamProj.teamProj.task.controller;

import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
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
    public ResponseEntity<String> completeTask(@PathVariable Integer taskId) {
        boolean updated=taskService.completeTask(taskId);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 taskId가 존재하지 않습니다.");

        }
        return ResponseEntity.ok("과제가 완료되었습니다.");
    }

}

