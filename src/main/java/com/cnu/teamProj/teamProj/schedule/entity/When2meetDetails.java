package com.cnu.teamProj.teamProj.schedule.entity;

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
@Getter
@Setter
public class When2meetDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "when2meet_id", referencedColumnName = "id")
    private When2meet when2meetId;

    public When2meetDetails(LocalDateTime startDate, LocalDateTime endDate, User userId, When2meet when2meetId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
        this.when2meetId = when2meetId;
    }
}
