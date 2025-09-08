package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface When2meetRepo extends JpaRepository<When2meet, Integer> {
    List<When2meet> findWhen2meetsByProjId(Project project);
}
