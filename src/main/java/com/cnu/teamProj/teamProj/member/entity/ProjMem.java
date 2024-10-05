package com.cnu.teamProj.teamProj.member.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Primary;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjMem {
    @Id
    private String id;

    private String projId;
}
