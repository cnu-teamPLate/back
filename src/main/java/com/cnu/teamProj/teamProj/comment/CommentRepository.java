package com.cnu.teamProj.teamProj.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findCommentsByTaskId(int taskId);
    ArrayList<Comment> findCommentsByUserId(String userId);
}
