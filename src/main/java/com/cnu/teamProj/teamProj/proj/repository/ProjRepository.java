package com.cnu.teamProj.teamProj.proj.repository;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface ProjRepository extends JpaRepository<Project, String> {
}
