package com.cnu.teamProj.teamProj.file.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Docs {
    @Id
    private String id;
    private String projId;
    private String url;
    private String title;
    private String detail;
    private ZonedDateTime uploadDate;
    private String category;

    public Docs() {

    }
}
