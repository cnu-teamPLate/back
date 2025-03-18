package com.cnu.teamProj.teamProj.proj.repository;

import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjMemRepository extends JpaRepository<ProjMem, String> {
    List<ProjMem> findProjMemsByProjId(String projId);
}
