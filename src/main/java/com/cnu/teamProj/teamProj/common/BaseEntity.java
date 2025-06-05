package com.cnu.teamProj.teamProj.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @LastModifiedDate
    private LocalDateTime uploadDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        if(uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
    }
}
