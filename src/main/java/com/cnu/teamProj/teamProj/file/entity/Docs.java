package com.cnu.teamProj.teamProj.file.entity;

import com.cnu.teamProj.teamProj.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Docs extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int fileId;

    private String id;
    private String projId;
    private String url;
    private String title;
    private String detail;

    private int category;
    private String filename;
    public Docs(String id, String projId, String url, String title, String detail, int category, String filename) {
        this.id = id;
        this.projId = projId;
        this.url = url;
        this.title = title;
        this.detail = detail;
        this.category = category;
        this.filename = filename;
    }

    public Docs() {

    }
}
