package com.cnu.teamProj.teamProj.task.controller;
import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.task.dto.TaskUpdateRequest;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.entity.Task;
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

import static com.cnu.teamProj.teamProj.util.SecurityUtil.getCurrentUser;

@RestController
@RequestMapping(value = "/task", produces = "application/json; charset=utf8") // 추가됨
@RequiredArgsConstructor
@Tag(name="TaskController", description = "과제 관리와 관련된 컨트롤러")
public class TaskController {
    private final TaskService taskService;
    private final DocsService docsService;

    @PostMapping("/post")
    @Operation(summary = "과제 생성 api", description = "⚠️과제 업로드 시 username, files, taskId 값은 필수값 아님")
    public ResponseEntity<?> createTask(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody TaskDTO taskDTO) {
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
    /*
    @PutMapping("{taskId}")
    public ResponseEntity<String> completeTask(@PathVariable Integer taskId) {
        boolean updated=taskService.completeTask(taskId);

        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 taskId가 존재하지 않습니다.");

        }
        return ResponseEntity.ok("과제가 완료되었습니다.");
    }
    */
    @PutMapping("/{taskId}")
    @Operation(description = "과제 완료 여부 체크")
    @ApiResponses(value = {
            //@ApiResponse(responseCode = "400", description = "응답에 필요한 필수 요청 값이 전달되지 않았습니다.",  content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 taskId가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "과제 담당자만 체크할 수 있습니다.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "예상치 못한 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "과제가 체크되었습니다. or 과제 체크가 해제되었습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> updateTaskCheckStatus(
            @PathVariable Integer taskId,
            @RequestParam("checkBox") int checkBox) {

        // 현재 로그인한 사용자
        String currentUser = getCurrentUser();

        Task task = taskService.findById(taskId);
        if (task == null) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "해당 taskId가 존재하지 않습니다.");
        }

        // 체크를 하는 경우 (1)
        if (checkBox == 1) {
            if (!task.getId().equals(currentUser)) {
                return ResultConstant.returnResultCustom(ResultConstant.NO_PERMISSION, "과제 담당자만 체크할 수 있습니다.");
            }
        }

        // 체크 또는 체크 해제 반영
        boolean updated = taskService.updateCheckStatus(taskId, checkBox == 1);
        if (!updated) {
            return ResultConstant.returnResult(ResultConstant.UNEXPECTED_ERROR);
        }

        String message = (checkBox == 1) ? "과제가 체크되었습니다." : "과제 체크가 해제되었습니다.";
        return ResultConstant.returnResultCustom(ResultConstant.OK, message);
    }

    @PutMapping("/edit/{taskId}")
    @Operation(summary = "과제 수정 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "해당 taskId가 존재하지 않거나 수정에 실패했습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200",  description = "과제가 성공적으로 수정되었습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> updateTask(
            @PathVariable(value = "taskId") Integer taskId,
            @RequestBody TaskUpdateRequest request) {
        boolean updated = taskService.updateTask(taskId, request);

        if (!updated) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하지 않는 taskId 혹은 userId 입니다");
        }

        return ResultConstant.returnResultCustom(ResultConstant.OK, "과제가 성공적으로 수정되었습니다.");
    }




}

