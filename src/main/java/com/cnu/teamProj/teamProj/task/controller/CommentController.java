package com.cnu.teamProj.teamProj.task.controller;

import com.cnu.teamProj.teamProj.task.dto.CommentDTO;
import com.cnu.teamProj.teamProj.task.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/save")
    public ResponseEntity save(@RequestBody CommentDTO commentDTO) {
        Long savedId = commentService.save(commentDTO);
        if (savedId != null) {
            return new ResponseEntity<>(commentService.findAll(commentDTO.getTaskId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<List<CommentDTO>> findAll(@PathVariable Long taskId) {
        return new ResponseEntity<>(commentService.findAll(taskId), HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@PathVariable Long id, @RequestBody CommentDTO commentDTO) {
        boolean isUpdated = commentService.update(id, commentDTO);
        if (isUpdated) {
            return new ResponseEntity<>(commentService.findAll(commentDTO.getTaskId()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to update comment", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        boolean isDeleted = commentService.delete(id);
        if (isDeleted) {
            return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to delete comment", HttpStatus.BAD_REQUEST);
        }
    }
}
