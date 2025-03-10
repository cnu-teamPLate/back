package com.cnu.teamProj.teamProj.schedule.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
public class Participants {
    @Id
    private String scheId;
    private String id;
    private String projId;

    public Participants() {
    }
}
