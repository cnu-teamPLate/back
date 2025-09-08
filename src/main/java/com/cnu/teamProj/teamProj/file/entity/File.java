package com.cnu.teamProj.teamProj.file.entity;

import com.cnu.teamProj.teamProj.security.entity.User;
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
    @Column(name = "file_id")
    private int fileId;

    private String filename;
    private String url;
    private String fileType;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;

    @LastModifiedDate
    private LocalDateTime uploadDate;

    public File(String filename, String url, String fileType, User user) {
        this.filename = filename;
        this.url = url;
        this.fileType = fileType;
        this.createdBy = user;
    }

    public File() {

    }
}
