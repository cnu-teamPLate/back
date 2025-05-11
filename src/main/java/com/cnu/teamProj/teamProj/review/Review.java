package com.cnu.teamProj.teamProj.review;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import com.cnu.teamProj.teamProj.security.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name="CLASS_ID", referencedColumnName = "classId")
    private ClassInfo classId;

    private String contents;

}
