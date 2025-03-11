package com.cnu.teamProj.teamProj.task.repository;

import com.cnu.teamProj.teamProj.task.entity.TaskComment;
import com.cnu.teamProj.teamProj.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findAllByTaskEntityOrderByIdDesc(Task taskEntity);
}
