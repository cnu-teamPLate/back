package com.cnu.teamProj.teamProj.task.dto;

import com.cnu.teamProj.teamProj.task.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDTO {
    private Long id;
    private String commentWriter;
    private String commentContents;
    private Long taskId;
    private LocalDateTime createdTime;

    public static CommentDTO toCommentDTO(Comment commentEntity) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(commentEntity.getId());
        commentDTO.setCommentWriter(commentEntity.getCommentWriter());
        commentDTO.setCommentContents(commentEntity.getCommentContents());
        commentDTO.setTaskId(Long.valueOf(commentEntity.getTaskEntity().getId()));
        commentDTO.setCreatedTime(commentEntity.getCreatedTime());
        return commentDTO;
    }
}
