package com.cnu.teamProj.teamProj.task.repository;

import com.cnu.teamProj.teamProj.task.entity.Comment;
import com.cnu.teamProj.teamProj.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTaskEntityOrderByIdDesc(Task taskEntity);
}
