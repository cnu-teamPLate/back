package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import com.cnu.teamProj.teamProj.schedule.entity.When2meetDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface When2meetDetailsRepo extends JpaRepository<When2meetDetails, Integer> {
    List<When2meetDetails> findAllByWhen2meetId(When2meet when2meet);
}
