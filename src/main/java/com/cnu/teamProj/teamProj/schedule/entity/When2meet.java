package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.dto.When2meetRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class When2meet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne
    @JoinColumn(name = "proj_id", referencedColumnName = "projId")
    private Project projId;

    public When2meet(When2meetRequestDto dto) {
        this.title = dto.getTitle();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
    }
}
