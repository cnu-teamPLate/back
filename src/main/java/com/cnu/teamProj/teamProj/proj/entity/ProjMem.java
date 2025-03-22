package com.cnu.teamProj.teamProj.proj.entity;

import com.cnu.teamProj.teamProj.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "proj_mem")
@IdClass(ProjMemId.class)
public class ProjMem {
    @Id
    @ManyToOne
    @JoinColumn(name="id")
    private User id;

    @Id
    @ManyToOne
    @JoinColumn(name="projId")
    private Project projId;
}
