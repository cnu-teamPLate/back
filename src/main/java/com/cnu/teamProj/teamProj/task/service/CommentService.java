package com.cnu.teamProj.teamProj.task.service;

import com.cnu.teamProj.teamProj.task.dto.CommentDTO;
import com.cnu.teamProj.teamProj.task.entity.TaskComment;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskCommentRepository;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final TaskCommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public Long save(CommentDTO commentDTO) {
        Optional<Task> optionalTaskEntity = taskRepository.findById(Math.toIntExact(commentDTO.getTaskId()));
        if (optionalTaskEntity.isPresent()) {
            Task taskEntity = optionalTaskEntity.get();
            TaskComment commentEntity = TaskComment.toSaveEntity(commentDTO, taskEntity);
            return commentRepository.save(commentEntity).getId();
        } else {
            return null;
        }
    }

    public List<CommentDTO> findAll(Long taskId) {
        Task taskEntity = taskRepository.findById(Math.toIntExact(taskId)).orElse(null);
        List<TaskComment> commentEntityList = commentRepository.findAllByTaskEntityOrderByIdDesc(taskEntity);
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (TaskComment commentEntity : commentEntityList) {
            commentDTOList.add(CommentDTO.toCommentDTO(commentEntity));
        }
        return commentDTOList;
    }

    public boolean update(Long id, CommentDTO commentDTO) {
        Optional<TaskComment> optionalCommentEntity = commentRepository.findById(id);
        if (optionalCommentEntity.isPresent()) {
            TaskComment commentEntity = optionalCommentEntity.get();
            commentEntity.setCommentContents(commentDTO.getCommentContents());
            commentEntity.setUpdatedTime(commentDTO.getCreatedTime());
            commentRepository.save(commentEntity);
            return true;
        }
        return false;
    }

    public boolean delete(Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
