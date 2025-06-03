package com.cnu.teamProj.teamProj.file.entity;

import com.cnu.teamProj.teamProj.common.BaseEntity;
import com.cnu.teamProj.teamProj.file.dto.DocsDto;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Doc extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String detail;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    private User createdBy;
    @ManyToOne
    @JoinColumn(name = "proj_id", referencedColumnName = "projId")
    private Project projId;
    private int category;

    public Doc(String title, String detail, int category) {
        this.title = title;
        this.detail = detail;
        this.category = category;
    }
}
