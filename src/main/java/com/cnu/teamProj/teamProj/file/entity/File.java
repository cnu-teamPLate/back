package com.cnu.teamProj.teamProj.file.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String filename;
    private String url;
    private String fileType;

    @LastModifiedDate
    private LocalDateTime uploadDate;

    public File(String filename, String url, String fileType) {
        this.filename = filename;
        this.url = url;
        this.fileType = fileType;
    }

    public File() {

    }
}
