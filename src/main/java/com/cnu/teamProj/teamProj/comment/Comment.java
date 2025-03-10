package com.cnu.teamProj.teamProj.comment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class Comment {
    @Id
    private String userId;
    private int taskId;
    private Integer checkbox;
    private String content;
    private ZonedDateTime dateTime;
}
