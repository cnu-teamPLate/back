package com.cnu.teamProj.teamProj.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class When2meetDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "when2meet_id", referencedColumnName = "id")
    private When2meet when2meetId;

    public When2meetDate(LocalDate startDate, LocalDate endDate, When2meet when2meetId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.when2meetId = when2meetId;
    }
}
