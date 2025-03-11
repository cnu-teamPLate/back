package com.cnu.teamProj.teamProj.task.service;

import com.cnu.teamProj.teamProj.task.dto.TaskDTO;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

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
            return dto;
        }).collect(Collectors.toList());
    }

    public String createTask(TaskDTO taskDTO) {
        try {
            // taskId가 숫자로 변환 가능한지 체크
            int parsedTaskId;
            try {
                parsedTaskId = Integer.parseInt(String.valueOf(taskDTO.getTaskId()));
            } catch (NumberFormatException e) {
                return "과제 ID(taskId)는 숫자로 변환할 수 없습니다.";
            }

            // Task 엔터티 생성 및 필드 설정
            Task task = new Task();
            task.setTaskId(parsedTaskId); // `TASK_ID`는 int
            task.setId(taskDTO.getId()); // 유저 아이디
            task.setProjId(taskDTO.getProjId()); // 프로젝트 ID
            task.setRole(taskDTO.getRole()); // 역할
            task.setDetail(taskDTO.getDetail()); // 상세 내용
            task.setDate(ZonedDateTime.now()); // 현재 시간
            task.setLevel(taskDTO.getLevel()); // 난이도
            task.setCate(taskDTO.getCate()); // 카테고리
            task.setCheckBox(taskDTO.getCheckBox()); // 체크 여부 (기본값 0)

            taskRepository.save(task);
            return "과제가 성공적으로 등록되었습니다.";
        } catch (Exception e) {
            return "과제 등록 중 오류 발생: " + e.getMessage();
        }
    }
}
