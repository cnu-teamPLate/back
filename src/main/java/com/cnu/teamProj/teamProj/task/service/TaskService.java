package com.cnu.teamProj.teamProj.task.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.dto.TaskUpdateRequest;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    @Autowired
    private DocsService docsService;
    private final UserRepository userRepository;
    /*
    public boolean completeTask(Integer taskId) {
        Optional<Task> optionalTask=taskRepository.findById(taskId);

        if (optionalTask.isPresent()) {
            Task task=optionalTask.get();
            task.setCheckBox(1);
            taskRepository.save(task);
            return true;
        }

        return false;
    }
    */

    public Task findById(Integer taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    public boolean updateCheckStatus(Integer taskId, boolean checked) {
        Task task = findById(taskId);
        if (task == null) return false;

        task.setCheckBox(checked ? 1 : 0);
        taskRepository.save(task);
        return true;
    }
    /*
    public List<TaskDTO> getTasksByProjIdAndId(String projId, String id) {
        List<Task> tasks = taskRepository.findByProjIdAndId(projId, id);
        return tasks.stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setTaskId(task.getTaskId());
            dto.setId(task.getId());
            dto.setProjId(task.getProjId());
            dto.setRole(task.getRole());
            dto.setCate(task.getCate());
            dto.setLevel(task.getLevel());
            dto.setDate(task.getDate());
            dto.setDetail(task.getDetail());
            dto.setCheckBox(task.getCheckBox());
            dto.setTaskName(task.getTaskName());
            return dto;
        }).collect(Collectors.toList());
    }
    *//*
    public List<TaskDTO> getTasksByProjIdAndId(String projId, String id) {
        List<Task> tasks = taskRepository.findByProjIdAndId(projId, id);

        return tasks.stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setTaskId(task.getTaskId());
            dto.setId(task.getId());
            dto.setProjId(task.getProjId());
            dto.setCate(task.getCate());
            dto.setLevel(task.getLevel());
            dto.setDate(task.getDate());
            dto.setDetail(task.getDetail());
            dto.setCheckBox(task.getCheckBox());
            dto.setTaskName(task.getTaskName());

            // 이름 조회해서 DTO에 set
            userRepository.findById(task.getId())
                    .ifPresent(user -> dto.setUserName(user.getUsername())); // 👈 여기 핵심!

            return dto;
        }).collect(Collectors.toList());
    }
*/
    public List<TaskDTO> getTasksByFlexibleParams(String projId, String id) {
        List<Task> tasks = new ArrayList<>();

        if (projId != null && id != null) {
            // projId + id
            tasks = taskRepository.findByProjIdAndId(projId, id);

        } else if (projId != null) {
            // projId만 있을 경우
            tasks = taskRepository.findAllByProjId(projId);

        } else if (id != null) {
            // id만 있을 경우
            tasks = taskRepository.findAllById(id);
        }

        return tasks.stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            dto.setTaskId(task.getTaskId());
            dto.setId(task.getId());
            dto.setProjId(task.getProjId());
            dto.setCate(task.getCate());
            dto.setLevel(task.getLevel());
            dto.setDate(task.getDate());
            dto.setDetail(task.getDetail());
            dto.setCheckBox(task.getCheckBox());
            dto.setTaskName(task.getTaskName());

            userRepository.findById(task.getId())
                    .ifPresent(user -> dto.setUserName(user.getUsername()));

            List<DocsViewResponseDto> docs = docsService.getDocsByUserAndProject(task.getProjId(), task.getId());

            List<FileDto> files = docs.stream()
                    .map(doc -> new FileDto(doc.getUrl(), doc.getFilename()))
                    .collect(Collectors.toList());

            dto.setFiles(files);


            return dto;
        }).collect(Collectors.toList());
    }

    public ResponseEntity<?> createTask(TaskDTO taskDTO) {
        try {
            // taskId가 숫자로 변환 가능한지 체크
   //         int parsedTaskId;
   //         try {
    //            parsedTaskId = Integer.parseInt(String.valueOf(taskDTO.getTaskId()));
     //       } catch (NumberFormatException e) {
       //         return "과제 ID(taskId)는 숫자로 변환할 수 없습니다.";
         //   }

            // Task 엔터티 생성 및 필드 설정
            Task task = new Task();
           // task.setTaskId(parsedTaskId); // `TASK_ID`는 int
            task.setId(taskDTO.getId()); // 유저 아이디
            task.setProjId(taskDTO.getProjId()); // 프로젝트 ID
            task.setRole(taskDTO.getRole()); // 역할
            task.setDetail(taskDTO.getDetail()); // 상세 내용
            task.setDate(ZonedDateTime.now()); // 현재 시간
            task.setLevel(taskDTO.getLevel()); // 난이도
            task.setCate(taskDTO.getCate()); // 카테고리
            task.setCheckBox(taskDTO.getCheckBox()); // 체크 여부 (기본값 0)
            task.setTaskName(taskDTO.getTaskName());

            taskRepository.save(task);
            return ResultConstant.returnResultCustom(ResultConstant.OK, "과제 등록 성공");
        } catch (Exception e) {
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "과제 등록 실패");
        }
    }

    public boolean updateTask(Integer taskId, TaskUpdateRequest request) {
        Task task = findById(taskId);
        if (task == null) {
            return false;
        }

        // description 수정
        task.setDescription(request.getDescription());

        // assignee 수정
        if (request.getAssigneeId() != null) {
            System.out.println(request.getAssigneeId());

            Optional<User> assigneeOpt = userRepository.findById(request.getAssigneeId());
            if (assigneeOpt.isPresent()) {
                task.setAssignee(assigneeOpt.get());
            } else {
                return false; // 없는 사용자
            }
        }

        taskRepository.save(task);
        return true;
    }
}
