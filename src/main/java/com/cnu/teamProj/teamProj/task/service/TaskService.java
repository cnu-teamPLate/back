package com.cnu.teamProj.teamProj.task.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.dto.DetailTaskDto;
import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjRepository projRepository;
    private final DocsService docsService;
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
//            int parsedTaskId;
//            try {
//                parsedTaskId = Integer.parseInt(String.valueOf(taskDTO.getTaskId()));
//            } catch (NumberFormatException e) {
//                return "과제 ID(taskId)는 숫자로 변환할 수 없습니다.";
//            }

            // Task 엔터티 생성 및 필드 설정
            Task task = new Task();
//            task.setTaskId(parsedTaskId); // `TASK_ID`는 int
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

    public ResponseEntity<?> getTaskByTaskId(String param) {
        //⬇️유효성 검사
        int taskId = 0;
        try {
            taskId = Integer.parseInt(param);
        } catch(Exception e) {
            return ResultConstant.returnResult(ResultConstant.NOT_EXIST);
        }
        Task task = taskRepository.findTaskByTaskId(taskId);
        if(task == null) return ResultConstant.returnResult(ResultConstant.NOT_EXIST);

        DetailTaskDto taskData = new DetailTaskDto(task);
        User user = userRepository.findById(task.getId()).orElse(null);
        Project project = projRepository.findProjectByProjId(task.getProjId());
        if(user == null || project == null) {
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "더 이상 존재하지 않는 유저 혹은 프로젝트의 과제 입니다");
        }

        taskData.setProjName(project.getProjName());
        taskData.setUserName(user.getUsername());
        taskData.setFiles(docsService.getFilesForTask(taskId));

        return new ResponseEntity<>(taskData, HttpStatus.OK);
    }
}
