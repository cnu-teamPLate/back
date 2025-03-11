package com.cnu.teamProj.teamProj.task.entity;

import com.cnu.teamProj.teamProj.task.dto.CommentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "task_comment")
public class TaskComment {
//주석
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String commentWriter;

    @Column(nullable = false)
    private String commentContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task taskEntity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedTime = LocalDateTime.now();

    public static TaskComment toSaveEntity(CommentDTO commentDTO, Task taskEntity) {
        TaskComment commentEntity = new TaskComment();
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        commentEntity.setTaskEntity(taskEntity);
        return commentEntity;
    }
}
