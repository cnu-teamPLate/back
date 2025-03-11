package com.cnu.teamProj.teamProj.task.controller;

import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/task", produces = "application/json; charset=utf8") // 추가됨
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/post")
    public ResponseEntity<String> createTask(@RequestBody TaskDTO taskDTO) {
        String result = taskService.createTask(taskDTO);

        if (result.startsWith("과제가 성공적으로 등록되었습니다")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    @GetMapping("/view")
    public ResponseEntity<?> getTasks(@RequestParam String projId, @RequestParam String id) {
        List<TaskDTO> tasks = taskService.getTasksByProjIdAndId(projId, id);

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

