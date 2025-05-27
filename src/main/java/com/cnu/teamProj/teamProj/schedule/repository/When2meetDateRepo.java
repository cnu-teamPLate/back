package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import com.cnu.teamProj.teamProj.schedule.entity.When2meetDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface When2meetDateRepo extends JpaRepository<When2meetDate, Integer> {
    List<When2meetDate> findWhen2meetDatesByWhen2meetId(When2meet when2meetId);
}
