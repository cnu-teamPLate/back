package com.cnu.teamProj.teamProj.proj.repository;

import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<ProjMem, String> {
    List<ProjMem> findProjMemsByProjId(String projId);
    Boolean existsByProjId(String projId);
    Boolean existsProjMemByIdAndProjId(String id, String projId);


}
